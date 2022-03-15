@file:Suppress("unused")

package top.anagke.auto_ark.operate

import top.anagke.auto_android.device.*
import top.anagke.auto_android.img.ocrWord
import top.anagke.auto_android.util.Pos
import top.anagke.auto_android.util.Rect
import top.anagke.auto_android.util.Size
import top.anagke.auto_android.util.minutes
import top.anagke.auto_ark.operate.OperatePoses.终端_活动
import top.anagke.auto_ark.tmpl
import top.anagke.auto_ark.today
import java.time.DayOfWeek

object OperatePoses {
    internal val 终端 = Pos(970, 200)
    internal val 终端_主题曲 = Pos(240, 670)
    internal val 终端_资源收集 = Pos(719, 670)
    internal val 终端_每周部署 = Pos(878, 670)

    internal val PR_X_1 = Pos(403, 438)
    internal val PR_X_2 = Pos(830, 258)
    internal val XX_5 = Pos(945, 177)

    internal val 终端_活动 = Pos(1096, 143)
}

object OperateTemplates {
    // 关卡准备页面，且代理指挥开启
    internal val 关卡信息界面_代理指挥开启 by tmpl(diff = 0.01)

    // 关卡准备页面，且代理指挥关闭
    internal val 关卡信息界面_代理指挥关闭 by tmpl(diff = 0.02)

    // 等待“开始行动”
    internal val 编队界面 by tmpl(diff = 0.01)

    // 理智不足
    internal val 理智不足_可使用药剂 by tmpl(diff = 0.01)

    // 理智不足，且即将过期
    internal val 理智不足_药剂即将到期 by tmpl(diff = 0.01)

    // 理智不足，且仅能使用源石补充
    internal val 理智不足_可使用源石 by tmpl(diff = 0.01)

    // 等待“作战结束”
    internal val 行动结束 by tmpl(diff = 0.01)

    // 等待“升级”
    internal val 等级提升 by tmpl(diff = 0.025)

    internal val 剿灭_行动结束 by tmpl(diff = 0.05)
    internal val 剿灭_已通过 by tmpl(diff = 0.01)
    internal val 剿灭_已刷满 by tmpl(diff = 0.0025)
}

object OperateOperations {
    internal val LS_5 = Operation("LS-5", "作战记录") {
        tap(OperatePoses.终端).sleep()
        tap(OperatePoses.终端_资源收集).nap()
        tap(643, 363).sleep() //战术演习
        tap(OperatePoses.XX_5).nap() //LS-5
    }
    internal val CE_5 = Operation("CE-5", "龙门币") {
        tap(OperatePoses.终端).sleep()
        tap(OperatePoses.终端_资源收集).nap()
        tap(438, 349).sleep() //资源保障
        tap(OperatePoses.XX_5).nap() //CE-5
    }
    internal val CA_5 = Operation("CA-5", "技巧概要") {
        tap(OperatePoses.终端).sleep()
        tap(OperatePoses.终端_资源收集).nap()
        tap(229, 357).sleep() //空中威胁
        tap(OperatePoses.XX_5).nap() //CA-5
    }

    private enum class 资源收集芯片 {
        固若金汤, 摧枯拉朽, 势不可挡, 身先士卒;

        fun where(dev: Device): Pos? {
            val cap = dev.cap()
            val chipTextRegion = listOf(
                Rect(Pos(423 + 207 * 0, 453), Size(137, 49)),
                Rect(Pos(423 + 207 * 1, 453), Size(137, 49)),
                Rect(Pos(423 + 207 * 2, 453), Size(137, 49)),
                Rect(Pos(423 + 207 * 3, 453), Size(137, 49))
            )
            return chipTextRegion.find {
                val image = cap.crop(it)
                val words = values().map(资源收集芯片::name)
                ocrWord(image, words = words) == name
            }?.center()
        }
    }

    internal val PR_A_1 = Operation("PR-A-1", "重装/医疗芯片") {
        进入_终端_资源收集_芯片()

        tap(资源收集芯片.固若金汤.where(this) ?: return@Operation).sleep()
        tap(OperatePoses.PR_X_1).nap()
    }
    internal val PR_B_1 = Operation("PR-B-1", "狙击/术士芯片") {
        进入_终端_资源收集_芯片()

        tap(资源收集芯片.摧枯拉朽.where(this) ?: return@Operation).sleep()
        tap(OperatePoses.PR_X_1).nap()
    }
    internal val PR_C_1 = Operation("PR-C-1", "先锋/辅助芯片") {
        进入_终端_资源收集_芯片()

        tap(资源收集芯片.势不可挡.where(this) ?: return@Operation).sleep()
        tap(OperatePoses.PR_X_1).nap()
    }
    internal val PR_D_1 = Operation("PR-D-1", "近卫/特种芯片") {
        进入_终端_资源收集_芯片()

        tap(资源收集芯片.身先士卒.where(this) ?: return@Operation).sleep()
        tap(OperatePoses.PR_X_1).nap()
    }

    internal val PR_A_2 = Operation("PR-A-2", "重装/医疗芯片组") {
        进入_终端_资源收集_芯片()

        tap(资源收集芯片.固若金汤.where(this) ?: return@Operation).sleep()
        tap(OperatePoses.PR_X_2).nap()
    }
    internal val PR_B_2 = Operation("PR-B-2", "狙击/术士芯片组") {
        进入_终端_资源收集_芯片()

        tap(资源收集芯片.摧枯拉朽.where(this) ?: return@Operation).sleep()
        tap(OperatePoses.PR_X_2).nap()
    }
    internal val PR_C_2 = Operation("PR-C-2", "先锋/辅助芯片组") {
        进入_终端_资源收集_芯片()

        tap(资源收集芯片.势不可挡.where(this) ?: return@Operation).sleep()
        tap(OperatePoses.PR_X_2).nap()
    }
    internal val PR_D_2 = Operation("PR-D-2", "近卫/特种芯片组") {
        进入_终端_资源收集_芯片()

        tap(资源收集芯片.身先士卒.where(this) ?: return@Operation).sleep()
        tap(OperatePoses.PR_X_2).nap()
    }

    private fun Device.进入_终端_资源收集_芯片() {
        tap(OperatePoses.终端).sleep()
        tap(OperatePoses.终端_资源收集).nap()
        dragv(640, 360, -600, 0)
        tap(1230, 50).nap()
    }

    internal val 剿灭作战 = Operation("当期剿灭作战", "合成玉", timeout = 15.minutes) {
        tap(OperatePoses.终端).sleep()
        tap(OperatePoses.终端_每周部署).nap()
        if (notMatch(OperateTemplates.剿灭_已刷满)) {
            tap(838, 385).sleep() //当期委托
            if (match(OperateTemplates.剿灭_已通过)) {
                //已通过当期委托
            } else {
                //尚未通过当期委托
                back().nap() //跳出关卡信息界面
                tap(1150, 680).nap() //切换剿灭作战
                tap(1184, 263).sleep() //选择龙门外环
                //选择龙门外环的原因是：如果一个账号尚未通过当期委托，那么很大概率是因为练度不够。
                //如果是因为练度不够，那么这个账号很可能也尚未通过龙门市区，所以保险起见，选择龙门外环。
            }
        }
    }

    internal val MAIN_1_7 = Operation("1-7", description = "固源岩") {
        tap(OperatePoses.终端).sleep() //进入“终端”
        tap(OperatePoses.终端_主题曲).nap() //进入“主题曲”
        tap(66, 132).nap() //进入“幻灭”
        tap(66, 132).nap() //进入“觉醒”

        dragv(950, 360, -950, 0) //确保处于“二次呼吸”
        tap(1200, 360).nap() //取消拖动

        tap(330, 375).sleep() //进入“黑暗时代（下）”

        dragv(160, 360, 4480, 0) //确保处于最左边
        dragv(950, 360, -1750, 0) //确保处于“1-7”
        tap(1092, 222).nap() //进入“1-7”
    }

    internal val IW_6 = Operation("IW-6", description = "将进酒、全新装置") {
        tap(终端_活动).sleep().sleep()
        tap(1078, 591).sleep()

        dragv(640, 360, -1280 * 3, 0)
        dragv(640, 360, 600, 0)
        tap(189, 531).nap() // IW-6
    }

    internal val IW_7 = Operation("IW-7", description = "将进酒、固源岩组") {
        tap(终端_活动).sleep().sleep()
        tap(1078, 591).sleep()

        dragv(640, 360, -1280 * 3, 0)
        dragv(640, 360, 600, 0)
        tap(393, 448).nap() // IW-7
    }

    internal val IW_8 = Operation("IW-8", description = "将进酒、RMA70-12") {
        tap(终端_活动).sleep().sleep()
        tap(1078, 591).sleep()

        dragv(640, 360, -1280 * 3, 0)
        dragv(640, 360, 600, 0)
        tap(624, 369).nap() // IW-8
    }

    internal val GA_6 = Operation("GA-6", description = "吾导先路、糖组") {
        tap(终端_活动).sleep().sleep()
        tap(1117, 471).sleep()

        dragv(640, 360, -1280 * 3, 0)
        dragv(640, 360, 1129, 0)
        tap(1240, 340).nap() // IW-8
    }
}

// 策略
// 1. 永远优先龙门币。
// 2. 然后刷 1-7 和 经验。
fun dailyOperation() = when (today()) {
    DayOfWeek.MONDAY -> OperateOperations.LS_5
    DayOfWeek.TUESDAY -> OperateOperations.CE_5
    DayOfWeek.WEDNESDAY -> OperateOperations.MAIN_1_7
    DayOfWeek.THURSDAY -> OperateOperations.CE_5
    DayOfWeek.FRIDAY -> OperateOperations.MAIN_1_7
    DayOfWeek.SATURDAY -> OperateOperations.CE_5
    DayOfWeek.SUNDAY -> OperateOperations.CE_5
}