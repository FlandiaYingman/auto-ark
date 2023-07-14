@file:Suppress("unused")

package top.anagke.auto_ark.operate

import top.anagke.auto_android.device.*
import top.anagke.auto_android.img.Pos
import top.anagke.auto_android.img.ocr
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
import top.anagke.auto_ark.operate.OperatePoses.终端_常态事务
import top.anagke.auto_ark.operate.OperatePoses.终端_活动
import top.anagke.auto_ark.operate.OperateTemplates.剿灭_作战进度奖励_已满
import top.anagke.auto_ark.operate.OperateTemplates.剿灭_已刷满
import top.anagke.auto_ark.operate.OperationType.剿灭
import top.anagke.auto_ark.resetInterface
import top.anagke.auto_ark.tmpl
import top.anagke.auto_ark.today
import java.time.DayOfWeek
import java.time.DayOfWeek.*
import java.util.function.Function

object OperatePoses {
    internal val 终端 = Pos(970, 200)
    internal val 终端_主题曲 = Pos(240, 670)
    internal val 终端_资源收集 = Pos(719, 670)
    internal val 终端_常态事务 = Pos(875, 670)

    internal val PR_X_1 = Pos(380, 415)
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

    // 关卡准备页面，可以全权委托，并且全权委托为开启状态
    internal val 关卡信息界面_全权委托开启 by tmpl(diff = 0.01)

    // 关卡准备页面，可以全权委托，并且全权委托为关闭状态
    internal val 关卡信息界面_全权委托关闭 by tmpl(diff = 0.01)

    internal val 全权委托确定界面 by tmpl()

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
    internal val 剿灭_作战进度奖励_已满 by tmpl()
}

object OperateOperations {

    @JvmStatic
    fun main(args: Array<String>) {
        App.defaultAutoArk().run {
            device.enter(MAIN_10_16)
        }
    }

    internal val 剿灭作战_当期 = Operation("当期剿灭作战", "合成玉", timeout = 30.minutes, type = 剿灭) {
        tap(终端).sleep()
        tap(终端_常态事务).nap()
        if (notMatch(剿灭_已刷满)) {
            tap(769, 456, desc = "当期剿灭作战").sleep()
        }
    }

    internal val 剿灭作战_龙门外环 = Operation("剿灭作战、龙门外环", "合成玉", timeout = 30.minutes, type = 剿灭) {
        tap(终端).sleep()
        tap(终端_常态事务).nap()
        if (notMatch(剿灭_已刷满)) {
            tap(769, 456, desc = "当期剿灭作战").sleep()
            back(description = "退出作战准备界面").nap()
            tap(1240, 649, desc = "切换剿灭").sleep()
            tap(1013, 265, desc = "龙门外环").sleep()
        }
    }

    internal val 剿灭作战_任意 = Operation("任意剿灭作战", description = "合成玉", timeout = 30.minutes, type = 剿灭) {
        enter(剿灭作战_当期)
        if (notMatch(剿灭_作战进度奖励_已满)) {
            resetInterface()
            enter(剿灭作战_龙门外环)
        }
    }

    internal val LS_6 = Operation("LS-6", "作战记录") {
        进入_终端_资源收集_材料()
        val pos = 资源收集_材料.战术演习.where(this) ?: return@Operation
        tap(pos, desc = "战术演习").nap()
        tap(OperatePoses.XX_6).nap() //LS-6
    }
    internal val CE_6 = Operation("CE-6", "龙门币") {
        进入_终端_资源收集_材料()
        val pos = 资源收集_材料.货物运送.where(this) ?: return@Operation
        tap(pos, desc = "货物运送").nap()
        tap(OperatePoses.XX_6).nap() //CE-6
    }
    internal val LS_5 = Operation("LS-5", "作战记录") {
        进入_终端_资源收集_材料()
        val pos = 资源收集_材料.战术演习.where(this) ?: return@Operation
        tap(pos, desc = "战术演习").nap()
        tap(OperatePoses.XX_5).nap() //LS-5
    }
    internal val CE_5 = Operation("CE-5", "龙门币") {
        进入_终端_资源收集_材料()
        val pos = 资源收集_材料.货物运送.where(this) ?: return@Operation
        tap(pos, desc = "货物运送").nap()
        tap(OperatePoses.XX_5).nap() //CE-5
    }
    internal val CA_5 = Operation("CA-5", "技巧概要") {
        进入_终端_资源收集_材料()
        val pos = 资源收集_材料.空中威胁.where(this) ?: return@Operation
        tap(pos, desc = "空中威胁").nap()
        tap(OperatePoses.XX_5_OLD).nap() //CA-5
    }

    private enum class 资源收集芯片(
        val levelChar: String,
        vararg val openDays: DayOfWeek
    ) {
        固若金汤("A", MONDAY, THURSDAY, FRIDAY, SUNDAY),
        摧枯拉朽("B", MONDAY, TUESDAY, FRIDAY, SATURDAY),
        势不可挡("C", WEDNESDAY, THURSDAY, SATURDAY, SUNDAY),
        身先士卒("D", TUESDAY, WEDNESDAY, SATURDAY, SUNDAY);

        companion object {
            val comparator: Comparator<资源收集芯片> = Comparator
                .comparing<资源收集芯片?, Boolean?> { it.isOpen() }
                .reversed()
                .thenComparing(Function { it.levelChar })
        }

        fun isOpen(): Boolean =
            today() in openDays

        fun index(): Int =
            资源收集芯片.values()
                .sortedWith(comparator)
                .indexOf(this)

        fun position() = Pos(495 + 205 * index(), 360)

    }

    internal val PR_A_1 = Operation("PR-A-1", "重装/医疗芯片", dropsPositions = listOf(0, 1)) {
        进入_终端_资源收集_芯片()

        tap(资源收集芯片.固若金汤.position()).sleep()
        tap(OperatePoses.PR_X_1).nap()
    }
    internal val PR_B_1 = Operation("PR-B-1", "狙击/术士芯片", dropsPositions = listOf(0, 1)) {
        进入_终端_资源收集_芯片()

        tap(资源收集芯片.摧枯拉朽.position()).sleep()
        tap(OperatePoses.PR_X_1).nap()
    }
    internal val PR_C_1 = Operation("PR-C-1", "先锋/辅助芯片", dropsPositions = listOf(0, 1)) {
        进入_终端_资源收集_芯片()

        tap(资源收集芯片.势不可挡.position()).sleep()
        tap(OperatePoses.PR_X_1).nap()
    }
    internal val PR_D_1 = Operation("PR-D-1", "近卫/特种芯片", dropsPositions = listOf(0, 1)) {
        进入_终端_资源收集_芯片()

        tap(资源收集芯片.身先士卒.position()).sleep()
        tap(OperatePoses.PR_X_1).nap()
    }

    internal val PR_A_2 = Operation("PR-A-2", "重装/医疗芯片组", dropsPositions = listOf(0, 1)) {
        进入_终端_资源收集_芯片()

        tap(资源收集芯片.固若金汤.position()).sleep()
        tap(OperatePoses.PR_X_2).nap()
    }
    internal val PR_B_2 = Operation("PR-B-2", "狙击/术士芯片组", dropsPositions = listOf(0, 1)) {
        进入_终端_资源收集_芯片()

        tap(资源收集芯片.摧枯拉朽.position()).sleep()
        tap(OperatePoses.PR_X_2).nap()
    }
    internal val PR_C_2 = Operation("PR-C-2", "先锋/辅助芯片组", dropsPositions = listOf(0, 1)) {
        进入_终端_资源收集_芯片()

        tap(资源收集芯片.势不可挡.position()).sleep()
        tap(OperatePoses.PR_X_2).nap()
    }
    internal val PR_D_2 = Operation("PR-D-2", "近卫/特种芯片组", dropsPositions = listOf(0, 1)) {
        进入_终端_资源收集_芯片()

        tap(资源收集芯片.身先士卒.position()).sleep()
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

    internal val MAIN_7_15 = Operation("7-15", description = "装置") {
        tap(终端).sleep() //进入“终端”
        tap(OperatePoses.终端_主题曲).nap() //进入“主题曲”
        tap(66, 132).nap() //进入“幻灭”

        swipeRight()
        tap(625, 375).sleep() //进入“黑暗时代（下）”

        swipeLeft()
        dragv(-1280 * 3, 0, 0.5)
        tap(473, 362).nap() //进入“7-15”
    }
    internal val MAIN_7_17 = Operation("7-17", description = "研磨石") {
        tap(终端).sleep() //进入“终端”
        tap(OperatePoses.终端_主题曲).nap() //进入“主题曲”
        tap(66, 132).nap() //进入“幻灭”

        swipeRight()
        tap(625, 375).sleep() //进入“黑暗时代（下）”

        swipeLeft()
        dragv(-1280 * 3, 0)
        tap(1251, 344).nap() //进入“7-17”
    }

    internal val MAIN_10_16 = Operation("10-16", description = "轻锰矿") {
        tap(终端).sleep() //进入“终端”
        tap(OperatePoses.终端_主题曲).nap() //进入“主题曲”
        tap(65, 130, "觉醒").nap()
        tap(65, 130, "觉醒").nap()
        tap(65, 555, "幻灭").nap()
        tap(65, 555, "残阳").nap()

        swipeLeft()
        tap(1265, 375, "破碎日冕").sleep()

        swipeRight()
        dragv(855, 0)
        tap(333, 478, "10-16").nap()
    }

    internal val IW_6 = ActOperation("IW-6", "将进酒", "全新装置") {
        tap(终端_活动).sleep().sleep()
        tap(1078, 591).sleep()

        dragv(640, 360, -1280 * 3, 0)
        dragv(640, 360, 600, 0)
        tap(189, 531).nap() // IW-6
    }
    internal val IW_7 = ActOperation("IW-7", "将进酒", "固源岩组") {
        tap(终端_活动).sleep().sleep()
        tap(1078, 591).sleep()

        dragv(640, 360, -1280 * 3, 0)
        dragv(640, 360, 600, 0)
        tap(393, 448).nap() // IW-7
    }
    internal val IW_8 = ActOperation("IW-8", "将进酒", "RMA70-12") {
        tap(终端_活动).sleep().sleep()
        tap(1078, 591).sleep()

        dragv(640, 360, -1280 * 3, 0)
        dragv(640, 360, 600, 0)
        tap(624, 369).nap() // IW-8
    }

    internal val GA_6 = ActOperation("GA-6", "吾导先路", "糖组") {
        tap(终端_活动).sleep().sleep()
        tap(1117, 471).sleep()

        dragv(640, 360, 1280 * 3, 0)
        dragv(640, 360, -1129, 0)
        tap(1240, 340).nap() // IW-8
    }

    internal val WD_6 = ActOperation("WD-6", "遗尘漫步", "聚酸酯组") {
        zone(1180, 550)

        swipev(640, 360, 1280 * 3, 0)
        dragv(640, 360, -1780, 0)

        tap(755, 350).nap()
    }
    internal val WD_7 = ActOperation("WD-7", "遗尘漫步", "酮凝集组") {
        zone(1180, 550)

        swipev(640, 360, 1280 * 3, 0)
        dragv(640, 360, -1780, 0)

        tap(880, 470).nap()
    }
    internal val WD_8 = ActOperation("WD-8", "遗尘漫步", "RMA70-12") {
        zone(1180, 550)

        swipev(640, 360, 1280 * 3, 0)
        dragv(640, 360, -1780, 0)

        tap(1200, 470).nap()
    }

    internal val SN_8 = ActOperation("SN-8", "愚人号", "异铁块") {
        tap(终端_活动).sleepl()
        tap(267, 495).sleepl() // 失落旗舰

        // 屏幕总是自动移动到最右侧，所以无需手动移动
        tap(381, 207).sleep() // SN-8
    }
    internal val SN_9 = ActOperation("SN-9", "愚人号", "轻锰矿") {
        tap(终端_活动).sleepl()
        tap(353, 571).sleepl() // 无名之海

        // 屏幕总是自动移动到最右侧，所以无需手动移动
        dragv(640, 360, 700, 0)
        tap(83, 442).sleep() // SN-9
    }
    internal val SN_10 = ActOperation("SN-10", "愚人号", "化合切削液") {
        tap(终端_活动).sleepl()
        tap(353, 571).sleepl() // 无名之海

        // 屏幕总是自动移动到最右侧，所以无需手动移动
        dragv(640, 360, 700, 0)
        tap(148, 339).sleep() // SN-10
    }

    internal val SV_9 = ActOperation("SV-9", "覆潮之下", "全新装置") {
        tap(终端_活动).sleepl()
        tap(1207, 447, desc = "荒败盐风").sleepl()

        swipev(640, 360, -2560, 0, speed = 10.0).sleep()
        tap(382, 267, desc = "SV-9").sleep()
    }

    internal val DV_8 = ActOperation("DV-8", "绿野幻梦", "炽合金") {
        tap(终端_活动, desc = "活动：绿野幻梦").sleepl()
        tap(1056, 276, desc = "Zone：试验基地").sleep()
        swipev(1280 / 2, 960 / 2, -1280 * 2, 0, speed = 10.0, desc = "拉到最右").nap()
        tap(410, 390, desc = "DV-8").nap()
    }
    internal val DV_7 = ActOperation("DV-7", "绿野幻梦", "扭转醇") {
        tap(终端_活动, desc = "活动：绿野幻梦").sleepl()
        tap(1056, 276, desc = "Zone：试验基地").sleep()
        swipev(1280 / 2, 960 / 2, -1280 * 2, 0, speed = 10.0, desc = "拉到最右").nap()
        tap(178, 447, desc = "DV-7").nap()
    }
    internal val DV_6 = ActOperation("DV-6", "绿野幻梦", "聚酸酯组") {
        tap(终端_活动, desc = "活动：绿野幻梦").sleepl()
        tap(1056, 276, desc = "Zone：试验基地").sleep()
        swipev(1280 / 2, 960 / 2, -1280 * 2, 0, speed = 10.0, desc = "拉到最右").nap()
        dragv(1280 / 2, 960 / 2, 323, 0, desc = "显示DV-6").nap()
        tap(410, 390, desc = "DV-6").nap()
    }

    private fun Device.IC() = zone(1094, 472)
    internal val IC_7 = ActOperation("IC-7", "理想城：夏日狂欢季", "酮凝集") {
        IC()
        swipeRight(400)
        tap(112, 390, desc = "IC-7")
    }
    internal val IC_8 = ActOperation("IC-8", "理想城：夏日狂欢季", "凝胶") {
        IC()
        swipeRight()
        tap(91, 295, desc = "IC-8")
    }
    internal val IC_9 = ActOperation("IC-9", "理想城：夏日狂欢季", "RMA70-12") {
        IC()
        swipeRight()
        tap(249, 458, desc = "IC-9")
    }

    private fun Device.BW() = zone(1181, 599)
    internal val BW_7 = ActOperation("BW-7", "好久不见", "聚酸酯组") {
        BW()
        tap(1064, 383, desc = "BW-7")
    }
    internal val BW_8 = ActOperation("BW-8", "好久不见", "酮凝集") {
        BW()
        tap(1071, 227, desc = "BW-8")
    }


    private fun Device.NL() = zone(997, 440)
    internal val NL_10 = ActOperation("NL-10", "长夜临光", "扭转醇") {
        NL()
        swipeRight()
        tap(428, 498, desc = "NL-10")
    }
    internal val NL_9 = ActOperation("NL-9", "长夜临光", "扭转醇") {
        NL()
        swipeRight()
        tap(341, 241, desc = "NL-9")
    }
    internal val NL_8 = ActOperation("NL-8", "长夜临光", "扭转醇") {
        NL()
        swipeRight()
        tap(203, 344, desc = "NL-8")
    }

    private fun Device.IS() = zone(1054, 492)
    internal val IS_8 = ActOperation("IS-8", "叙拉古人", "固源岩组") {
        IS()
        repeat(2) {
            tap(1212, 659).nap()
            tap(1212, 384).nap()
        }
        swipev(81, 621, 0, -720).nap()
        tap(81, 397).nap()
    }

    private fun Device.WB() = zone(1098, 655)
    internal val WB_7 = ActOperation("WB-7", "登临意", "酮凝集组") {
        WB()
        swipeRight()
        tap(159, 468).nap()
    }
    internal val WB_8 = ActOperation("WB-8", "登临意", "轻锰矿") {
        WB()
        swipeRight()
        tap(369, 358).nap()
    }
    internal val WB_9 = ActOperation("WB-9", "登临意", "晶体元件") {
        WB()
        swipeRight()
        tap(555, 234).nap()
    }

    private fun Device.CF() = zone(1138, 568)
    internal val CF_8 = ActOperation("CF-8", "落叶逐火", "无") {
        CF()
        swipeRight()
        tap(246, 311).nap()
    }

    private fun Device.CW() = zone(1108, 204)
    internal val CW_8 = ActOperation("CW-8", "孤星", "酮凝集组") {
        CW()
        swipeLeft(-1380)
        tap(969, 323).nap()
    }
    internal val CW_9 = ActOperation("CW-9", "孤星", "半自然溶剂") {
        CW()
        swipeLeft(-1380)
        tap(969, 432).nap()
    }
    internal val CW_10 = ActOperation("CW-10", "孤星", "炽合金") {
        CW()
        swipeLeft(-1380)
        tap(1249, 438).nap()
    }

    private fun Device.FD() = zone(1200, 500)
    internal val FD_7 = ActOperation("FD-7", "眠于树影之中", "异铁组") {
        FD()
        swipeRight()
        tap(640, 600).nap()
    }
    internal val FD_8 = ActOperation("FD-8", "眠于树影之中", "全新装置") {
        FD()
        swipeRight()
        tap(640, 280).nap()
    }

    private fun Device.swipeLeft(offset: Int = 0) {
        swipev(1280 * 4 + offset, 0, speed = 10.0, desc = "最左").nap()
        dragv(offset, 0)
    }

    private fun Device.swipeRight(offset: Int = 0) {
        swipev(-1280 * 4 + offset, 0, speed = 10.0, desc = "最右").nap()
        dragv(offset, 0)
    }

    private fun Device.zone(x: Int, y: Int) {
        tap(x, y).sleep()
    }

}

// 策略
// 1. 永远优先龙门币。
// 2. 然后刷 1-7 和 经验。
fun dailyOps(level: 资源关种类) = when (today()) {
    MONDAY -> when (level) {
        资源关种类.LEVEL_5 -> LS_5
        资源关种类.LEVEL_6 -> LS_6
    }

    TUESDAY -> when (level) {
        资源关种类.LEVEL_5 -> CE_5
        资源关种类.LEVEL_6 -> CE_6
    }

    WEDNESDAY -> CA_5

    THURSDAY -> when (level) {
        资源关种类.LEVEL_5 -> CE_5
        资源关种类.LEVEL_6 -> CE_6
    }

    FRIDAY -> MAIN_1_7

    SATURDAY -> when (level) {
        资源关种类.LEVEL_5 -> LS_5
        资源关种类.LEVEL_6 -> LS_6
    }

    SUNDAY -> CA_5
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
            val word = ocr(image)
            word == name
        }?.center()
    }
}