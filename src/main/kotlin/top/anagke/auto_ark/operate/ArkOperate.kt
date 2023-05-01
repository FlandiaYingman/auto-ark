package top.anagke.auto_ark.operate


import org.tinylog.kotlin.Logger
import top.anagke.auto_android.device.*
import top.anagke.auto_ark.App
import top.anagke.auto_ark.ArkModule
import top.anagke.auto_ark.AutoArk
import top.anagke.auto_ark.operate.OperateOperations.剿灭作战_龙门外环
import top.anagke.auto_ark.operate.OperateOperations.当期剿灭作战
import top.anagke.auto_ark.operate.OperateResult.合成玉已刷满
import top.anagke.auto_ark.operate.OperateResult.理智已不足
import top.anagke.auto_ark.operate.OperateStrategy.*
import top.anagke.auto_ark.operate.OperateTemplates.全权委托确定界面
import top.anagke.auto_ark.operate.OperateTemplates.关卡信息界面_代理指挥关闭
import top.anagke.auto_ark.operate.OperateTemplates.关卡信息界面_代理指挥开启
import top.anagke.auto_ark.operate.OperateTemplates.关卡信息界面_全权委托关闭
import top.anagke.auto_ark.operate.OperateTemplates.关卡信息界面_全权委托开启
import top.anagke.auto_ark.operate.OperateTemplates.剿灭_行动结束
import top.anagke.auto_ark.operate.OperateTemplates.理智不足_可使用源石
import top.anagke.auto_ark.operate.OperateTemplates.理智不足_可使用药剂
import top.anagke.auto_ark.operate.OperateTemplates.理智不足_药剂即将到期
import top.anagke.auto_ark.operate.OperateTemplates.等级提升
import top.anagke.auto_ark.operate.OperateTemplates.编队界面
import top.anagke.auto_ark.operate.OperateTemplates.行动结束
import top.anagke.auto_ark.resetInterface

class ArkOperate(
    auto: AutoArk,
) : ArkModule(auto) {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            farm(OperateOperations.CW_10, 2)
        }

        private fun farm(o: Operation, n: Int) {
            val ark = App.defaultAutoArk()
            val operate = ArkOperate(ark)
            operate.farm(o, n)
        }
    }

    private val conf = config.行动配置

    override fun init() {
        // To initialize the operations
        OperateOperations
    }

    override fun run() {
        Logger.info("运行模块：刷副本")
        farmAnnihilation()
        farmPlan()
        farmDaily()
    }

    override val name = "行动模块"

    private fun farmAnnihilation() {
        Logger.info("刷剿灭委托：${conf.刷剿灭}")
        if (!conf.刷剿灭) return

        if (conf.刷当期剿灭) {
            farm(当期剿灭作战, 1)
        } else {
            farm(剿灭作战_龙门外环, 1)
        }
    }

    private fun farmPlan() {
        Logger.info("刷计划副本：${conf.刷计划}")
        if (!conf.刷计划) return

        for (plan in savedata.farmingPlans) {
            for (planEntry in plan.entries.shuffled()) {
                val (operationName, farmTimes) = planEntry
                if (farmTimes == 0) continue

                val operation = Operation.operations.find { it.name == operationName }
                if (operation != null) {
                    val actualFarmTimes = farm(operation, farmTimes)
                    planEntry.setValue(farmTimes - actualFarmTimes)
                } else {
                    throw IllegalArgumentException("operation of name '$operationName' not found")
                }
            }
        }
    }

    private fun farmDaily() {
        Logger.info("刷日常副本：${conf.刷日常}")
        if (!conf.刷日常) return

        farm(dailyOps(conf.资源关种类))
    }


    private fun farm(operation: Operation, farmTimes: Int = Int.MAX_VALUE): Int = device.run {
        Logger.info("刷副本：$operation，预计刷 $farmTimes 次")

        val successful = enterOperation(operation)
        if (!successful) {
            Logger.info("刷副本：$operation，完毕，实际刷 ${0} 次")
            return@run 0
        }

        var actualTimes = 0
        farmLoop@ for (i in 0 until farmTimes) {
            val result = operateOperation(operation)
            when (result) {
                理智已不足 -> break@farmLoop
                合成玉已刷满 -> continue@farmLoop
                else -> {}
            }
            actualTimes++
            Logger.info("刷副本：$operation 中，实际刷 $actualTimes/$farmTimes 次")
        }
        resetInterface()

        Logger.info("刷副本：$operation，完毕，实际刷 $actualTimes/$farmTimes 次")
        actualTimes
    }

    private fun Device.enterOperation(operation: Operation): Boolean {
        Logger.info("进入关卡：$operation")
        return when (enter(operation)) {
            OperationState.OPEN -> {
                Logger.info("进入关卡：$operation，完毕")
                assert(
                    关卡信息界面_代理指挥开启, 关卡信息界面_代理指挥关闭,
                )
                true
            }

            OperationState.NOT_OPEN -> {
                Logger.info("进入关卡：$operation，关卡未开放，退出")
                false
            }
        }
    }

    private fun Device.operateOperation(operation: Operation): OperateResult {
        Logger.info("代理指挥关卡：$operation，理智策略：${conf.理智策略}")
        assert(
            关卡信息界面_代理指挥开启, 关卡信息界面_代理指挥关闭, 关卡信息界面_全权委托开启, 关卡信息界面_全权委托关闭
        )
        when (operation.type) {
            OperationType.常规, OperationType.活动 -> {
                if (match(关卡信息界面_代理指挥关闭)) {
                    Logger.info("代理指挥关卡：$operation，代理指挥关闭，开启")
                    tap(1067, 592, desc = "开启“代理指挥”")
                }
            }

            OperationType.剿灭 -> {
                if (match(关卡信息界面_全权委托关闭)) {
                    Logger.info("代理指挥关卡：$operation，全权委托关闭，开启")
                    tap(909, 593, desc = "开启“全权委托”")
                }
            }
        }

        tap(1078, 661, desc = "开始行动")
        await(编队界面, 全权委托确定界面, 理智不足_可使用药剂, 理智不足_可使用源石)

        var strategy = conf.理智策略
        if (strategy == IFF_EXPIRE_SOON) {
            strategy = if (match(理智不足_药剂即将到期)) POTION else WAIT
        }

        match(理智不足_可使用药剂, 理智不足_可使用源石)
        if (matched(理智不足_可使用药剂) && strategy.canUsePotion() || matched(理智不足_可使用源石) && strategy.canUseOriginite()) {
            Logger.info("代理指挥关卡：$operation，理智不足，恢复")
            tap(1088, 577, desc = "恢复理智")
            await(关卡信息界面_代理指挥开启, 关卡信息界面_全权委托开启)
            tap(1078, 661, desc = "开始行动")
            await(编队界面, 全权委托确定界面)
        }

        match(理智不足_可使用药剂, 理智不足_可使用源石)
        if (matched(理智不足_可使用药剂, 理智不足_可使用源石)) {
            Logger.info("代理指挥关卡：$operation，理智不足，退出")
            tap(783, 580)
            await(关卡信息界面_代理指挥开启, 关卡信息界面_全权委托开启)
            return 理智已不足
        }

        if (match(全权委托确定界面)) {
            tap(1141, 659, desc = "确认使用").sleep()
            if (match(全权委托确定界面)) {
                Logger.info("代理指挥关卡：$operation，合成玉已刷满，退出")
                back(description = "返回关卡信息界面")
                return 合成玉已刷满
            }
        } else {
            tap(1103, 522, desc = "开始行动").sleep()
        }
        await(行动结束, 等级提升, 剿灭_行动结束, timeout = operation.timeout)

        if (matched(剿灭_行动结束)) {
            tap(640, 360).sleep()
            tap(640, 360).sleep()
        }
        tap(640, 360).sleep()
        tap(640, 360).nap()
        await(关卡信息界面_代理指挥开启)
        Logger.info("代理指挥关卡：$operation，完毕")
        return OperateResult.成功
    }

}