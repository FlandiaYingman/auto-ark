package top.anagke.auto_ark.ark

import kotlinx.serialization.Serializable
import top.anagke.auto_ark.adb.Ops
import top.anagke.auto_ark.adb.assert
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.back
import top.anagke.auto_ark.adb.match
import top.anagke.auto_ark.adb.ops
import top.anagke.auto_ark.adb.tap
import top.anagke.auto_ark.ark.SanityStrategy.WAIT

@Serializable
data class OperateProps(
    val sanityStrategy: SanityStrategy = WAIT,
)

private val props = arkProps.operateProps

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
    ORIGINITE,
}

// 关卡准备页面，且代理指挥开启
val enabledAutoDeploy by template("enabledAutoDeploy.png")
// 关卡准备页面，且代理指挥关闭
val disabledAutoDeploy by template("disabledAutoDeploy.png")
// 等待“开始行动”
val awaitOperationStart by template("awaitOperationStart.png")

// 等待“理智不足”
val awaitSanityEmpty by template("awaitSanityEmpty.png")
// 等待“理智不足”，且仅能使用源石补充
val awaitSanityEmptyOriginite by template("awaitSanityEmptyOriginite.png")

// 等待“作战结束”
val awaitOperationFinish by template("awaitOperationFinish.png")
// 等待“升级”
val awaitOperationLevelUp by template("awaitOperationLevelUp.png")

// 退出游戏提示
val isBackToEnd by template("isBackToEnd.png")


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
        await(enabledAutoDeploy, disabledAutoDeploy)
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
        assert(enabledAutoDeploy, disabledAutoDeploy)
        do {
            back()
            val mainScreen = match(atMainScreen)
            val backToEnd = match(isBackToEnd)
            if (backToEnd) {
                back()
            }
        } while (!mainScreen && !backToEnd)
    }
}

/**
 * 自动化代理指挥完成关卡。
 *
 * 开始于：关卡准备界面。
 * 结束于：主界面。
 */
fun autoOperation(): Ops {
    return ops {
        assert(enabledAutoDeploy, disabledAutoDeploy).let {
            if (it === disabledAutoDeploy) tap(1067, 592) // 开启“代理指挥”
        }

        tap(1078, 661)
        await(awaitOperationStart, awaitSanityEmpty, awaitSanityEmptyOriginite).let {
            if ((it == awaitSanityEmpty &&
                        props.sanityStrategy == WAIT) ||
                (it == awaitSanityEmptyOriginite &&
                        (props.sanityStrategy == WAIT || props.sanityStrategy == SanityStrategy.POTION))
            ) {
                tap(783, 580)
                await(enabledAutoDeploy)
                return@ops false
            }
            if ((it == awaitSanityEmpty &&
                        (props.sanityStrategy == SanityStrategy.POTION || props.sanityStrategy == SanityStrategy.ORIGINITE)) ||
                (it == awaitSanityEmptyOriginite && props.sanityStrategy == SanityStrategy.ORIGINITE)
            ) {
                tap(1088, 577) // 恢复理智
                await(enabledAutoDeploy)
                tap(1078, 661)
                await(awaitOperationStart)
            }
        }

        tap(1103, 522)
        await(awaitOperationFinish, awaitOperationLevelUp).let { tap(640, 360) }

        tap(640, 360)
        await(enabledAutoDeploy)
        return@ops true
    }
}