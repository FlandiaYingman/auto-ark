@file:Suppress("unused")

package top.anagke.auto_ark.operate

import top.anagke.auto_android.device.*
import top.anagke.auto_android.img.Pos
import top.anagke.auto_android.img.ocrWord
import top.anagke.auto_android.util.Rect
import top.anagke.auto_android.util.Size
import top.anagke.auto_android.util.minutes
import top.anagke.auto_ark.App
import top.anagke.auto_ark.operate.OperateOperations.CA_5
import top.anagke.auto_ark.operate.OperateOperations.CE_5
import top.anagke.auto_ark.operate.OperateOperations.CE_6
import top.anagke.auto_ark.operate.OperateOperations.LS_5
import top.anagke.auto_ark.operate.OperateOperations.LS_6
import top.anagke.auto_ark.operate.OperateOperations.MAIN_1_7
import top.anagke.auto_ark.operate.OperatePoses.终端
import top.anagke.auto_ark.operate.OperatePoses.终端_副活动
import top.anagke.auto_ark.operate.OperatePoses.终端_常态事务
import top.anagke.auto_ark.operate.OperatePoses.终端_活动
import top.anagke.auto_ark.operate.OperateTemplates.剿灭_已刷满
import top.anagke.auto_ark.operate.OperationType.剿灭
import top.anagke.auto_ark.tmpl
import top.anagke.auto_ark.today
import java.time.DayOfWeek

object OperatePoses {
    internal val 终端 = Pos(970, 200)
    internal val 终端_主题曲 = Pos(240, 670)
    internal val 终端_资源收集 = Pos(719, 670)
    internal val 终端_常态事务 = Pos(875, 670)

    internal val PR_X_1 = Pos(328, 443)
    internal val PR_X_2 = Pos(783, 247)

    // CA-5 等。
    internal val XX_5_OLD = Pos(879, 178)

    // LS-5、CE-5。
    internal val XX_5 = Pos(918, 292)

    // LS-6、CE-6。
    internal val XX_6 = Pos(968, 175)

    internal val 终端_活动 = Pos(1096, 143)
    internal val 终端_副活动 = Pos(1096, 243)
}

object OperateTemplates {
    // 关卡准备页面，且代理指挥开启
    internal val 关卡信息界面_代理指挥开启 by tmpl(diff = 0.01)

    // 关卡准备页面，且代理指挥关闭
    internal val 关卡信息界面_代理指挥关闭 by tmpl(diff = 0.01)

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
    internal val 剿灭_已刷满 by tmpl(diff = 0.001)
}

object OperateOperations {

    @JvmStatic
    fun main(args: Array<String>) {
        App.defaultAutoArk().run {
            device.enter(MAIN_1_7)
        }
    }

    internal val 剿灭作战 = Operation("当期剿灭作战", "合成玉", timeout = 30.minutes, type = 剿灭) {
        tap(终端).sleep()
        tap(终端_常态事务).nap()
        if (notMatch(剿灭_已刷满)) {
            tap(769, 456, description = "当期剿灭作战").sleep()
        }
    }

    internal val LS_6 = Operation("LS-6", "作战记录") {
        进入_终端_资源收集_材料()
        val pos = 资源收集_材料.战术演习.where(this) ?: return@Operation
        tap(pos, description = "战术演习").nap()
        tap(OperatePoses.XX_6).nap() //LS-6
    }
    internal val CE_6 = Operation("CE-6", "龙门币") {
        进入_终端_资源收集_材料()
        val pos = 资源收集_材料.货物运送.where(this) ?: return@Operation
        tap(pos, description = "货物运送").nap()
        tap(OperatePoses.XX_6).nap() //CE-6
    }
    internal val LS_5 = Operation("LS-5", "作战记录") {
        进入_终端_资源收集_材料()
        val pos = 资源收集_材料.战术演习.where(this) ?: return@Operation
        tap(pos, description = "战术演习").nap()
        tap(OperatePoses.XX_5).nap() //LS-5
    }
    internal val CE_5 = Operation("CE-5", "龙门币") {
        进入_终端_资源收集_材料()
        val pos = 资源收集_材料.货物运送.where(this) ?: return@Operation
        tap(pos, description = "货物运送").nap()
        tap(OperatePoses.XX_5).nap() //CE-5
    }
    internal val CA_5 = Operation("CA-5", "技巧概要") {
        进入_终端_资源收集_材料()
        val pos = 资源收集_材料.空中威胁.where(this) ?: return@Operation
        tap(pos, description = "空中威胁").nap()
        tap(OperatePoses.XX_5_OLD).nap() //CA-5
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
        tap(终端).sleep()
        tap(OperatePoses.终端_资源收集).nap()
        dragv(640, 360, -600, 0)
        tap(1230, 50).nap()
    }

    private fun Device.进入_终端_资源收集_材料() {
        tap(终端).sleep()
        tap(OperatePoses.终端_资源收集).nap()
        swipev(640, 360, 600, 0, speed = 1.0).nap()
    }


    internal val MAIN_1_7 = Operation("1-7", description = "固源岩") {
        tap(终端).sleep() //进入“终端”
        tap(OperatePoses.终端_主题曲).nap() //进入“主题曲”
        tap(66, 132).nap() //进入“幻灭”
        tap(66, 132).nap() //进入“觉醒”

        dragv(950, 360, -950, 0) //确保处于“二次呼吸”
        tap(1200, 360).nap() //取消拖动

        tap(330, 375).sleep() //进入“黑暗时代（下）”

        swipev(160, 360, 4480, 0, 10.0) //确保处于最左边
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

        dragv(640, 360, 1280 * 3, 0)
        dragv(640, 360, -1129, 0)
        tap(1240, 340).nap() // IW-8
    }

    internal val WD_6 = Operation("WD-6", description = "遗尘漫步、聚酸酯组") {
        enterSecondaryEvent(1180, 550)

        swipev(640, 360, 1280 * 3, 0)
        dragv(640, 360, -1780, 0)

        tap(755, 350).nap()
    }
    internal val WD_7 = Operation("WD-7", description = "遗尘漫步、酮凝集组") {
        enterSecondaryEvent(1180, 550)

        swipev(640, 360, 1280 * 3, 0)
        dragv(640, 360, -1780, 0)

        tap(880, 470).nap()
    }
    internal val WD_8 = Operation("WD-8", description = "遗尘漫步、RMA70-12") {
        enterSecondaryEvent(1180, 550)

        swipev(640, 360, 1280 * 3, 0)
        dragv(640, 360, -1780, 0)

        tap(1200, 470).nap()
    }

    internal val SN_8 = Operation("SN-8", description = "愚人号、异铁块") {
        tap(终端_活动).sleepl()
        tap(267, 495).sleepl() // 失落旗舰

        // 屏幕总是自动移动到最右侧，所以无需手动移动
        tap(381, 207).sleep() // SN-8
    }
    internal val SN_9 = Operation("SN-9", description = "愚人号、轻锰矿") {
        tap(终端_活动).sleepl()
        tap(353, 571).sleepl() // 无名之海

        // 屏幕总是自动移动到最右侧，所以无需手动移动
        dragv(640, 360, 700, 0)
        tap(83, 442).sleep() // SN-9
    }
    internal val SN_10 = Operation("SN-10", description = "愚人号、化合切削液") {
        tap(终端_活动).sleepl()
        tap(353, 571).sleepl() // 无名之海

        // 屏幕总是自动移动到最右侧，所以无需手动移动
        dragv(640, 360, 700, 0)
        tap(148, 339).sleep() // SN-10
    }

    internal val SV_9 = Operation("SV-9", description = "覆潮之下、全新装置") {
        tap(终端_活动).sleepl()
        tap(1207, 447, description = "荒败盐风").sleepl()

        swipev(640, 360, -2560, 0, speed = 10.0).sleep()
        tap(382, 267, description = "SV-9").sleep()
    }

    internal val DV_8 = Operation("DV-8", description = "绿野幻梦、炽合金") {
        tap(终端_活动, description = "活动：绿野幻梦").sleepl()
        tap(1056, 276, description = "Zone：试验基地").sleep()
        swipev(1280 / 2, 960 / 2, -1280 * 2, 0, speed = 10.0, description = "拉到最右").nap()
        tap(410, 390, description = "DV-8").nap()
    }
    internal val DV_7 = Operation("DV-7", description = "绿野幻梦、扭转醇") {
        tap(终端_活动, description = "活动：绿野幻梦").sleepl()
        tap(1056, 276, description = "Zone：试验基地").sleep()
        swipev(1280 / 2, 960 / 2, -1280 * 2, 0, speed = 10.0, description = "拉到最右").nap()
        tap(178, 447, description = "DV-7").nap()
    }
    internal val DV_6 = Operation("DV-6", description = "绿野幻梦、聚酸酯组") {
        tap(终端_活动, description = "活动：绿野幻梦").sleepl()
        tap(1056, 276, description = "Zone：试验基地").sleep()
        swipev(1280 / 2, 960 / 2, -1280 * 2, 0, speed = 10.0, description = "拉到最右").nap()
        dragv(1280 / 2, 960 / 2, 323, 0, description = "显示DV-6").nap()
        tap(410, 390, description = "DV-6").nap()
    }

    internal val DH_7 = Operation("DH-7", description = "多索雷斯假日") {
        enterPrimaryEvent(947, 327)
        swipev(4096, 0, speed = 10.0, description = "移动到最左").nap()
        dragv(-775, 0, description = "显示DH-7").nap()
        tap(1240, 317, description = "DH-7").nap()
    }


    private fun Device.enterPrimaryEvent(x: Int, y: Int) {
        tap(终端_活动).sleep().sleep()
        tap(x, y).sleep().sleep()
    }

    private fun Device.enterSecondaryEvent(x: Int, y: Int) {
        tap(终端_副活动).sleep().sleep()
        tap(x, y).sleep().sleep()
    }
}

// 策略
// 1. 永远优先龙门币。
// 2. 然后刷 1-7 和 经验。
fun dailyOps() = when (today()) {
    DayOfWeek.MONDAY -> listOf(LS_6, LS_5)
    DayOfWeek.TUESDAY -> listOf(CE_6, CE_5)
    DayOfWeek.WEDNESDAY -> listOf(CA_5)
    DayOfWeek.THURSDAY -> listOf(CE_6, CE_5)
    DayOfWeek.FRIDAY -> listOf(MAIN_1_7)
    DayOfWeek.SATURDAY -> listOf(CE_6, CE_5)
    DayOfWeek.SUNDAY -> listOf(CE_6, CE_5)
}

private enum class 资源收集_材料 {
    战术演习, 货物运送, 空中威胁, 资源保障, 粉碎防御;

    val chipTextRegion = listOf(
        Rect(Pos(94 + 207 * 0, 453), Size(137, 49)),
        Rect(Pos(94 + 207 * 1, 453), Size(137, 49)),
        Rect(Pos(94 + 207 * 2, 453), Size(137, 49)),
        Rect(Pos(94 + 207 * 3, 453), Size(137, 49)),
        Rect(Pos(94 + 207 * 4, 453), Size(137, 49))
    )

    fun where(dev: Device): Pos? {
        val cap = dev.cap()
        return chipTextRegion.find {
            val image = cap.crop(it)
            val words = values().map(资源收集_材料::name)
            val word = ocrWord(image, words = words)
            word == name
        }?.center()
    }
}