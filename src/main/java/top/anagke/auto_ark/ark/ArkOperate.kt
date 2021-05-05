package top.anagke.auto_ark.ark

import kotlinx.serialization.Serializable
import mu.KotlinLogging
import top.anagke.auto_ark.adb.Ops
import top.anagke.auto_ark.adb.OpsType
import top.anagke.auto_ark.adb.assert
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.back
import top.anagke.auto_ark.adb.matched
import top.anagke.auto_ark.adb.notMatch
import top.anagke.auto_ark.adb.ops
import top.anagke.auto_ark.adb.opsType
import top.anagke.auto_ark.adb.tap
import top.anagke.auto_ark.ark.SanityStrategy.WAIT

@Serializable
data class OperateProps(
    val sanityStrategy: SanityStrategy = WAIT,
)

private val props = arkProps.operateProps

private val log = KotlinLogging.logger {}


/**
 * 在自动化操作时遵循的理智策略。
 */
enum class SanityStrategy {
    /**
     * 不使用任何恢复理智的道具。
     */
    WAIT,
    /**
     * 使用应急理智顶液和应急理智合剂等**非源石**道具恢复理智。
     */
    POTION,
    /**
     * 使用源石恢复理智。隐含[POTION]，即：优先使用**非原石**道具，只有当不存在**非原石**道具时，才会使用源石。
     */
    ORIGINITE;

    fun canUsePotion() = this == POTION || this == ORIGINITE
    fun canUseOriginite() = this == ORIGINITE
}

enum class OperateResult {
    SUCCESS,
    EMPTY_SANITY
}


// 关卡准备页面，且代理指挥开启
private val atPrepareScreen = template("operate/atPrepareScreen.png", diff = 0.01)
// 关卡准备页面，且代理指挥关闭
private val isAutoDeployDisabled = template("operate/isAutoDeployDisabled.png", diff = 0.01)
// 等待“开始行动”
private val atFormationScreen = template("operate/atFormationScreen.png", diff = 0.01)

// 理智不足
private val popupSanityEmpty = template("operate/popupSanityEmpty.png", diff = 0.01)
// 理智不足，且仅能使用源石补充
private val popupSanityEmptyOriginite = template("operate/popupSanityEmptyOriginite.png", diff = 0.01)

// 等待“作战结束”
private val atCompleteScreen = template("operate/atCompleteScreen.png", diff = 0.05)
// 等待“升级”
private val popupLevelUp = template("operate/popupLevelUp.png")


fun autoLastOperation(): Ops {
    return ops {
        device(lastOperation())
        while (device(autoOperation()) == OperateResult.SUCCESS);
        device(exitOperation())
    }
}

/**
 * 进入最后一次完成的关卡的关卡准备界面。
 *
 * 开始于：主界面。
 * 结束于：关卡准备界面。
 */
fun lastOperation(): Ops {
    return ops {
        assert(atMainScreen)
        tap(970, 203) //终端
        tap(1121, 597) //前往上一次作战
        await(atPrepareScreen, isAutoDeployDisabled)
    }
}

/**
 * 退出关卡准备界面到主界面。
 *
 * 开始于：关卡准备界面。
 * 结束于：主界面。
 */
fun exitOperation(): Ops {
    return ops {
        assert(atPrepareScreen, isAutoDeployDisabled)
        while (notMatch(atMainScreen)) {
            back()
        }
    }
}

/**
 * 自动化代理指挥完成关卡。
 *
 * 开始于：关卡准备界面。
 * 结束于：主界面。
 */
fun autoOperation(): OpsType<OperateResult> {
    val strategy = props.sanityStrategy
    return opsType {
        log.info { "自动化代理指挥完成关卡，检测进入准备界面" }

        assert(atPrepareScreen, isAutoDeployDisabled)
        if (matched(isAutoDeployDisabled)) {
            log.info { "检测到代理指挥关闭，自动开启代理指挥" }
            tap(1067, 592) // 开启“代理指挥”
        }

        log.info { "开始行动，等待进入编队界面" }
        tap(1078, 661)
        await(atFormationScreen, popupSanityEmpty, popupSanityEmptyOriginite)

        if (matched(popupSanityEmpty) && strategy.canUsePotion() ||
            matched(popupSanityEmptyOriginite) && strategy.canUseOriginite()
        ) {
            log.info { "理智不足，恢复理智" }
            tap(1088, 577) // 恢复理智
            await(atPrepareScreen)
            tap(1078, 661)
            await(atFormationScreen)
        }

        if (matched(popupSanityEmpty, popupSanityEmptyOriginite)) {
            log.info { "理智不足，返回准备界面" }
            tap(783, 580)
            await(atPrepareScreen)
            return@opsType OperateResult.EMPTY_SANITY
        }

        log.info { "开始行动，等待行动结束" }
        tap(1103, 522)
        await(atCompleteScreen, popupLevelUp)
        tap(640, 360, delay = 1000)

        log.info { "行动结束，等待返回准备页面" }
        tap(640, 360)
        await(atPrepareScreen)
        return@opsType OperateResult.SUCCESS
    }
}
