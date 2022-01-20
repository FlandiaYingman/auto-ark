package top.anagke.auto_ark.riic

import kotlinx.serialization.Serializable
import top.anagke.auto_android.*
import top.anagke.auto_android.util.Pos
import top.anagke.auto_ark.jumpOut
import top.anagke.auto_ark.tmpl
import top.anagke.auto_ark.主界面

@Serializable
data class RiicConfig(
    val autoAssigning: Boolean = true,
    val autoAssignControlCenter: Boolean = false,
)

class ArkRiic(
    private val device: Device,
) : AutoModule {

    companion object {
        private val 基建界面 by tmpl()
        private val 自有库无线索 by tmpl()
        private val 干员选择界面 by tmpl(diff = 0.01)
        private val 进驻总览界面 by tmpl(diff = 0.01)

        @JvmStatic
        fun main(args: Array<String>) {
            ArkRiic(Device()).assign()
        }
    }

    override fun run() = device.run {
        assert(主界面)

        enterRiic()
        collect()
        jumpOut()

        enterRiic()
        assign()
        jumpOut()
    }

    private fun Device.enterRiic() {
        tap(985, 625) //进入基建
        await(基建界面)
        sleep()
    }

    private fun collect() = device.apply {
        tap(1203, 132).nap() //打开基建提醒（下方位置）
        repeat(3) { tap(239, 693).nap() } //收取贸易站产物、制造站产物和干员信赖
        tap(1136, 94).nap() //关闭基建提醒

        tap(1203, 92).nap() //打开基建提醒（一般位置）
        repeat(3) { tap(239, 693).nap() } //收取贸易站产物、制造站产物和干员信赖
        tap(1136, 94).nap() //关闭基建提醒

        tap(1047, 234).sleep().sleep() //打开会客室
        tap(414, 617).sleep() //进入详情
        tap(1197, 388).sleep() //进入传递线索
        whileNotMatch(自有库无线索) {
            tap(74, 183).nap() //选择第一个线索
            tap(1194, 135).sleep() //传递给第一个好友
        }
        back().sleep() // 返回会客室
        tap(1196, 180).sleep() //进入收集线索
        tap(805, 574).sleep() //领取会客室线索
    }

    private fun assign() = device.apply {
        tap(74, 118) //进驻总览
        await(进驻总览界面)

        //前两个宿舍
        repeat(2) {
            dragd(1200, 415, 0, -880) //一个宿舍距离
            shift(Pos(670, 200), 5, canPopup = true) //宿舍
        }

        //后两个宿舍
        dragd(1200, 415, 0, -880) //一个宿舍距离
        shift(Pos(670, 240), 5, canPopup = true) //第三个宿舍
        shift(Pos(670, 600), 5, canPopup = true) //第四个宿舍

        swipe(1200, 415, 1200, 415 + 2048, 500).nap()

        dragd(1200, 415, 0, -195) //一个房间距离
        shift(Pos(670, 200), 2) //第一个房间

        repeat(3) {
            dragd(1200, 415, 0, -245) //一层距离
            repeat(3) {
                shift(Pos(670, 200), 3) //贸易站、制造站或发电站
                dragd(1200, 415, 0, -195) //一个房间距离
            }
            if (it < 3 - 1) {
                dragd(1200, 415, 0, -195) //一个房间距离
                shift(Pos(670, 200), 3) //辅助设施
            }
        }
    }


    private fun shift(room: Pos, limit: Int, canPopup: Boolean = false) = device.apply {
        tap(room)
        await(干员选择界面)

        val operatorPos = listOf(
            Pos(490, 253),
            Pos(478, 446),
            Pos(597, 249),
            Pos(607, 464),
            Pos(732, 256),
            Pos(775, 501),
            Pos(931, 266),
            Pos(913, 445),
            Pos(1042, 235),
            Pos(1033, 454),
        )
        taps(operatorPos.subList(0, 2 * limit))

        if (canPopup) tap(1180, 675).nap() //确认
        tap(1180, 675) //确认
        await(进驻总览界面)
    }

}

