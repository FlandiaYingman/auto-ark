package top.anagke.auto_ark.operate

import mu.KotlinLogging
import top.anagke.auto_android.*
import top.anagke.auto_ark.AutoArkCache
import top.anagke.auto_ark.arkDayOfWeek
import top.anagke.auto_ark.jumpOut
import top.anagke.auto_ark.operate.OperateLevel.Companion.CA_5
import top.anagke.auto_ark.operate.OperateLevel.Companion.CE_5
import top.anagke.auto_ark.operate.OperateLevel.Companion.LS_5
import top.anagke.auto_ark.operate.OperateLevel.Companion.operateLevel
import top.anagke.auto_ark.operate.OperateLevel.Companion.剿灭作战
import top.anagke.auto_ark.operate.OperateResult.EMPTY_SANITY
import top.anagke.auto_ark.operate.OperateStrategy.*
import top.anagke.auto_ark.operate.OperateTemplates.关卡信息界面_代理指挥关闭
import top.anagke.auto_ark.operate.OperateTemplates.关卡信息界面_代理指挥开启
import top.anagke.auto_ark.operate.OperateTemplates.剿灭_行动结束
import top.anagke.auto_ark.operate.OperateTemplates.理智不足_可使用源石
import top.anagke.auto_ark.operate.OperateTemplates.理智不足_可使用药剂
import top.anagke.auto_ark.operate.OperateTemplates.理智不足_药剂即将到期
import top.anagke.auto_ark.operate.OperateTemplates.等级提升
import top.anagke.auto_ark.operate.OperateTemplates.编队界面
import top.anagke.auto_ark.operate.OperateTemplates.行动结束
import java.time.DayOfWeek.*

val dailyLevel = when (arkDayOfWeek) {
    MONDAY -> LS_5
    TUESDAY -> CE_5
    WEDNESDAY -> CA_5
    THURSDAY -> CE_5
    FRIDAY -> CA_5
    SATURDAY -> CE_5
    SUNDAY -> CE_5
}

private val logger = KotlinLogging.logger {}

class ArkOperate(
    private val device: Device,
    private val config: OperateConfig,
    private val cache: AutoArkCache,
) : AutoModule {

    override fun run() {
        logger.info { "运行模块：刷副本" }
        farmAnnihilation()
        farmPlan()
        farmDaily()
    }

    private fun farmAnnihilation() {
        logger.info { "刷剿灭委托：${config.doFarmAnnihilation}" }
        if (!config.doFarmAnnihilation) return

        farm(剿灭作战, 1)
    }

    private fun farmPlan() {
        logger.info { "刷计划副本：${config.doFarmPlan}" }
        if (!config.doFarmPlan) return

        val farmingPlan = OperateLevel.levels
            .map(OperateLevel::name)
            .associateWith { 0 }
            .toMutableMap()
        farmingPlan.putAll(cache.farmingPlan)
        cache.farmingPlan = farmingPlan

        for (entry in farmingPlan) {
            val levelName = entry.key
            val farmTimes = entry.value
            if (farmTimes == 0) continue

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
        logger.info { "进入关卡：$level" }

        val state = level.enter(this)
        when (state) {
            LevelEntryState.UNOPENED -> {
                logger.info { "进入关卡：$level，关卡未开放，退出" }
                return false
            }
            LevelEntryState.FAILED -> {
                logger.info { "进入关卡：$level，进入失败，退出" }
                return false
            }
            LevelEntryState.SUCCESSFUL -> {
            }
        }

        assert(关卡信息界面_代理指挥开启, 关卡信息界面_代理指挥关闭)
        logger.info { "进入关卡：$level，完毕" }
        return true
    }

    private fun Device.operateLevel(level: OperateLevel): OperateResult {
        logger.info { "代理指挥关卡：$level，理智策略：$config.strategy" }
        assert(关卡信息界面_代理指挥开启, 关卡信息界面_代理指挥关闭)
        if (matched(关卡信息界面_代理指挥关闭)) {
            logger.info { "代理指挥关卡：$level，代理指挥关闭，开启" }
            tap(1067, 592) // 开启“代理指挥”
        }

        tap(1078, 661)
        await(编队界面, 理智不足_可使用药剂, 理智不足_可使用源石)

        var strategy = config.strategy
        if (strategy == IFF_EXPIRE_SOON) {
            strategy = (if (match(理智不足_药剂即将到期)) POTION else WAIT)
        }

        which(理智不足_可使用药剂, 理智不足_可使用源石)
        if (matched(理智不足_可使用药剂) && strategy.canUsePotion() ||
            matched(理智不足_可使用源石) && strategy.canUseOriginite()
        ) {
            logger.info { "代理指挥关卡：$level，理智不足，恢复" }
            tap(1088, 577) // 恢复理智
            await(关卡信息界面_代理指挥开启)
            tap(1078, 661)
            await(编队界面)
        }

        which(理智不足_可使用药剂, 理智不足_可使用源石)
        if (matched(理智不足_可使用药剂, 理智不足_可使用源石)) {
            logger.info { "代理指挥关卡：$level，理智不足，退出" }
            tap(783, 580)
            await(关卡信息界面_代理指挥开启)
            return EMPTY_SANITY
        }

        tap(1103, 522)
        await(行动结束, 等级提升, 剿灭_行动结束, timeout = level.timeout)
        if (matched(剿灭_行动结束)) {
            tap(640, 360).nap()
            tap(640, 360).nap()
        }
        tap(640, 360).nap()

        tap(640, 360).nap()
        await(关卡信息界面_代理指挥开启)
        logger.info { "代理指挥关卡：$level，完毕" }
        return OperateResult.SUCCESS
    }


    @Suppress("unused")
    fun farmThis(farmTimes: Int) {
        val level = operateLevel("本关卡") {}
        farm(level, farmTimes)
    }

}