package top.anagke.auto_ark.operate


import org.tinylog.kotlin.Logger
import top.anagke.auto_android.device.*
import top.anagke.auto_ark.ArkModule
import top.anagke.auto_ark.AutoArk
import top.anagke.auto_ark.jumpOut
import top.anagke.auto_ark.operate.OperateOperations.剿灭作战
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

class ArkOperate(
    auto: AutoArk,
) : ArkModule(auto) {

    private val operateConfig = config.operateConfig

    override fun init() {
        // To initialize the operations
        OperateOperations

        Operation.operations
            .map { it.name }
            .associateWith { 0 }
            .toMutableMap()
            .apply {
                putAll(savedata.farmingPlan)
                savedata.farmingPlan = this
            }
    }

    override fun run() {
        Logger.info("运行模块：刷副本")
        farmAnnihilation()
        farmPlan()
        farmDaily()
    }

    override val name = "行动模块"

    private fun farmAnnihilation() {
        Logger.info("刷剿灭委托：${operateConfig.doFarmAnnihilation}")
        if (!operateConfig.doFarmAnnihilation) return

        farm(剿灭作战, 1)
    }

    private fun farmPlan() {
        Logger.info("刷计划副本：${operateConfig.doFarmPlan}")
        if (!operateConfig.doFarmPlan) return

        for (entry in savedata.farmingPlan) {
            val operationName = entry.key
            val farmTimes = entry.value
            if (farmTimes == 0) continue

            val operation = Operation.operations.find { it.name == operationName }
            if (operation != null) {
                val actualFarmTimes = farm(operation, farmTimes)
                entry.setValue(farmTimes - actualFarmTimes)
            } else {
                throw IllegalArgumentException("operation of name '$operationName' not found")
            }
        }
    }

    private fun farmDaily() {
        Logger.info("刷日常副本：${operateConfig.doFarmDaily}")
        if (!operateConfig.doFarmDaily) return

        farm(dailyOperation())
    }


    private fun farm(operation: Operation, farmTimes: Int = Int.MAX_VALUE): Int = device.run {
        Logger.info("刷副本：$operation，预计刷 $farmTimes 次")

        val successful = enterOperation(operation)
        if (!successful) {
            Logger.info("刷副本：$operation，完毕，实际刷 ${0} 次")
            return@run 0
        }

        var actualTimes = 0
        for (i in 0 until farmTimes) {
            val result = operateOperation(operation)
            if (result == EMPTY_SANITY) break
            actualTimes++
        }
        jumpOut()

        Logger.info("刷副本：$operation，完毕，实际刷 $actualTimes 次")
        actualTimes
    }

    private fun Device.enterOperation(operation: Operation): Boolean {
        Logger.info("进入关卡：$operation")
        return when (enter(operation)) {
            OperationState.OPEN -> {
                Logger.info("进入关卡：$operation，完毕")
                assert(关卡信息界面_代理指挥开启, 关卡信息界面_代理指挥关闭)
                true
            }
            OperationState.NOT_OPEN -> {
                Logger.info("进入关卡：$operation，关卡未开放，退出")
                false
            }
        }
    }

    private fun Device.operateOperation(operation: Operation): OperateResult {
        Logger.info("代理指挥关卡：$operation，理智策略：${operateConfig.strategy}")
        assert(关卡信息界面_代理指挥开启, 关卡信息界面_代理指挥关闭)
        if (matched(关卡信息界面_代理指挥关闭)) {
            Logger.info("代理指挥关卡：$operation，代理指挥关闭，开启")
            tap(1067, 592) // 开启“代理指挥”
        }

        tap(1078, 661)
        await(编队界面, 理智不足_可使用药剂, 理智不足_可使用源石)

        var strategy = operateConfig.strategy
        if (strategy == IFF_EXPIRE_SOON) {
            strategy = (if (match(理智不足_药剂即将到期)) POTION else WAIT)
        }

        which(理智不足_可使用药剂, 理智不足_可使用源石)
        if (matched(理智不足_可使用药剂) && strategy.canUsePotion() || matched(理智不足_可使用源石) && strategy.canUseOriginite()) {
            Logger.info("代理指挥关卡：$operation，理智不足，恢复")
            tap(1088, 577) // 恢复理智
            await(关卡信息界面_代理指挥开启)
            tap(1078, 661)
            await(编队界面)
        }

        which(理智不足_可使用药剂, 理智不足_可使用源石)
        if (matched(理智不足_可使用药剂, 理智不足_可使用源石)) {
            Logger.info("代理指挥关卡：$operation，理智不足，退出")
            tap(783, 580)
            await(关卡信息界面_代理指挥开启)
            return EMPTY_SANITY
        }

        tap(1103, 522)
        await(行动结束, 等级提升, 剿灭_行动结束, timeout = operation.timeout)
        if (matched(剿灭_行动结束)) {
            tap(640, 360).nap()
            tap(640, 360).nap()
        }
        tap(640, 360).nap()

        tap(640, 360).nap()
        await(关卡信息界面_代理指挥开启)
        Logger.info("代理指挥关卡：$operation，完毕")
        return OperateResult.SUCCESS
    }

}