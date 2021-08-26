package top.anagke.auto_ark.ark.operate

import kotlinx.serialization.Serializable
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.assert
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.delay
import top.anagke.auto_ark.adb.matched
import top.anagke.auto_ark.ark.appConfig
import top.anagke.auto_ark.ark.arkToday
import top.anagke.auto_ark.ark.atMainScreen
import top.anagke.auto_ark.ark.jumpOut
import top.anagke.auto_ark.ark.log
import top.anagke.auto_ark.ark.operate.OperateResult.EMPTY_SANITY
import top.anagke.auto_ark.ark.operate.OperateStrategy.WAIT
import java.time.DayOfWeek.*

@Serializable
data class OperateConfig(
    val strategy: OperateStrategy = WAIT,
    val isOnEvent: Boolean = false,
)


val dailyLevel = when (arkToday) {
    MONDAY -> LS_5
    TUESDAY -> CE_5
    WEDNESDAY -> CA_5
    THURSDAY -> CE_5
    FRIDAY -> CA_5
    SATURDAY -> CE_5
    SUNDAY -> LS_5
}

fun Device.autoOperate() {
    val config = appConfig.operateConfig

//    enter(annihilation)
//    operateOne(config.strategy)
//    jumpOut()

    enter(dailyLevel)
    operateAll(config.strategy, timeout = dailyLevel.timeout)
    jumpOut()
}


/**
 * 进入指定的关卡。
 *
 * 开始于：主界面。
 * 结束于：准备界面。
 */
fun Device.enter(operation: OperateLevel) {
    log.info { "进入关卡：${operation.name}" }
    assert(atMainScreen)
    operation.enter(this)

    assert(atPrepareScreen, atPrepareScreen_autoDeployDisabled)
}

/**
 * 自动化代理指挥完成关卡，一次。
 *
 * 开始于：准备界面。
 * 结束于：准备界面。
 */
fun Device.operateOne(strategy: OperateStrategy, timeout: Long): OperateResult {
    log.info { "代理指挥关卡，检测进入准备界面" }
    assert(atPrepareScreen, atPrepareScreen_autoDeployDisabled)
    if (matched(atPrepareScreen_autoDeployDisabled)) {
        log.info { "代理指挥关闭，开启代理指挥" }
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
        return EMPTY_SANITY
    }

    log.info { "开始行动，等待行动结束" }
    tap(1103, 522)
    await(atCompleteScreen, popupLevelUp, timeout = timeout)
    tap(640, 360)
    delay(1000)

    log.info { "行动结束，等待返回准备页面" }
    tap(640, 360)
    await(atPrepareScreen)
    return OperateResult.SUCCESS
}

/**
 * 自动化代理指挥完成关卡，直到理智不足。
 *
 * 开始于：准备界面。
 * 结束于：准备界面。
 */
fun Device.operateAll(strategy: OperateStrategy, timeout: Long ){
    @Suppress("ControlFlowWithEmptyBody")
    while (operateOne(strategy, timeout = timeout) != EMPTY_SANITY);
}
