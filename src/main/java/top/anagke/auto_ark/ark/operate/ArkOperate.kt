package top.anagke.auto_ark.ark.operate

import mu.KotlinLogging
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.assert
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.matched
import top.anagke.auto_ark.adb.nap
import top.anagke.auto_ark.ark.AutoArk
import top.anagke.auto_ark.ark.AutoArk.Companion.arkToday
import top.anagke.auto_ark.ark.AutoArk.Companion.log
import top.anagke.auto_ark.ark.appConfig
import top.anagke.auto_ark.ark.atMainScreen
import top.anagke.auto_ark.ark.jumpOut
import top.anagke.auto_ark.ark.operate.OperateLevel.Companion.CA_5
import top.anagke.auto_ark.ark.operate.OperateLevel.Companion.CE_5
import top.anagke.auto_ark.ark.operate.OperateLevel.Companion.LS_5
import top.anagke.auto_ark.ark.operate.OperateLevel.Companion.annihilation
import top.anagke.auto_ark.ark.operate.OperateResult.EMPTY_SANITY
import java.time.DayOfWeek.*

val dailyLevel = when (arkToday) {
    MONDAY -> LS_5
    TUESDAY -> CE_5
    WEDNESDAY -> CA_5
    THURSDAY -> CE_5
    FRIDAY -> CA_5
    SATURDAY -> CE_5
    SUNDAY -> LS_5
}

class ArkOperate(
    private val device: Device,
    private val config: OperateConfig,
) {

    private val logger = KotlinLogging.logger {}

    fun auto() {
        logger.info { "运行模块：刷副本" }
        farmAnnihilation()
        farmPlan()
        farmDaily()
    }

    private fun farmPlan() {
        logger.info { "刷计划副本：${config.doFarmPlan}" }
        if (!config.doFarmPlan) return

        for (entry in config.farmingPlan) {
            val levelName = entry.key
            val farmTimes = entry.value
            if (farmTimes <= 0) continue

            val level = OperateLevel.levelsMap[levelName]
            if (level != null) {
                val actualFarmTimes = farm(level, farmTimes)
                entry.setValue(farmTimes - actualFarmTimes)
            } else {
                throw IllegalArgumentException("level of name '$levelName' not found")
            }
        }
    }

    private fun farmDaily() {
        logger.info { "刷日常副本：${config.doFarmDaily}" }
        if (!config.doFarmDaily) return

        farm(dailyLevel)
    }

    private fun farmAnnihilation() {
        logger.info { "刷剿灭委托：${config.doFarmAnnihilation}" }
        if (!config.doFarmAnnihilation) return

        farm(annihilation, 1)
    }


    private fun farm(level: OperateLevel, farmTimes: Int = Int.MAX_VALUE): Int = device.run {
        logger.info { "刷副本：$level，预计刷 $farmTimes 次" }

        val successful = enterLevel(level)
        if (!successful) {
            logger.info { "刷副本：$level，完毕，实际刷 ${0} 次" }
            return@run 0
        }

        var actualTimes = 0
        for (i in 0 until farmTimes) {
            val result = operateLevel(level)
            if (result == EMPTY_SANITY) break
            actualTimes++
        }
        jumpOut()

        logger.info { "刷副本：$level，完毕，实际刷 $actualTimes 次" }
        actualTimes
    }

    private fun Device.enterLevel(level: OperateLevel): Boolean {
        log.info { "进入关卡：$level" }
        assert(atMainScreen)
        val successful = level.entry(this)
        if (!successful) {
            log.info { "进入关卡：$level，无法进入，退出" }
            return false
        }

        assert(atPrepareScreen, atPrepareScreen_autoDeployDisabled)
        log.info { "进入关卡：$level，完毕" }
        return true
    }

    private fun Device.operateLevel(level: OperateLevel): OperateResult {
        log.info { "代理指挥关卡：$level，理智策略：${config.strategy}" }
        assert(atPrepareScreen, atPrepareScreen_autoDeployDisabled)
        if (matched(atPrepareScreen_autoDeployDisabled)) {
            log.info { "代理指挥关卡：$level，代理指挥关闭，开启" }
            tap(1067, 592) // 开启“代理指挥”
        }

        tap(1078, 661)
        await(atFormationScreen, popupSanityEmpty, popupSanityEmptyOriginite)

        if (matched(popupSanityEmpty) && config.strategy.canUsePotion() ||
            matched(popupSanityEmptyOriginite) && config.strategy.canUseOriginite()
        ) {
            log.info { "代理指挥关卡：$level，理智不足，恢复" }
            tap(1088, 577) // 恢复理智
            await(atPrepareScreen)
            tap(1078, 661)
            await(atFormationScreen)
        }

        if (matched(popupSanityEmpty, popupSanityEmptyOriginite)) {
            log.info { "代理指挥关卡：$level，理智不足，退出" }
            tap(783, 580)
            await(atPrepareScreen)
            return EMPTY_SANITY
        }

        tap(1103, 522)
        await(atCompleteScreen, popupLevelUp, atAnnihilationCompleteScreen, timeout = level.timeout)
        if (matched(atAnnihilationCompleteScreen)) {
            tap(640, 360).nap()
            tap(640, 360).nap()
        }
        tap(640, 360).nap()

        tap(640, 360).nap()
        await(atPrepareScreen)
        log.info { "代理指挥关卡：$level，完毕" }
        return OperateResult.SUCCESS
    }

}

fun main() {
    AutoArk(appConfig).autoOperate()
}