package dev.flandia.ark.riic

import dev.flandia.android.device.*
import dev.flandia.android.img.Pos
import dev.flandia.ark.*

class ArkRIIC(
    auto: AutoArk,
) : ArkModule(auto) {

    companion object {
        private val 基建界面 by tmpl()
        private val 自有库无线索 by tmpl()
        private val 干员选择界面 by tmpl()
        private val 进驻总览界面 by tmpl()
        private val 贸易站_存在订单 by tmpl()
        private val 会客室_存在可用线索 by tmpl()
        private val 会客室_线索交流活动完毕 by tmpl()

        val 心情增序 by tmpl()
        val 未进驻筛选 by tmpl()

        @JvmStatic
        fun main(args: Array<String>) {
            ArkRIIC(App.defaultAutoArk()).run()
        }
    }

    override val name: String = "基建模块"

    private val conf = config.基建配置

    override fun run(): Unit = device.run {
        assert(主界面)

        进入基建()
        收取基建()
        退出基建()

        进入基建()
        换班()
        退出基建()
    }

    private fun 进入基建() = device.apply {
        tap(985, 625) //进入基建
        await(基建界面)
        sleep()
    }

    private fun 退出基建() = device.apply {
        resetInterface()
    }

    private fun 收取基建() = device.apply {
        收取基建产出()
        收取会客室()
        resetInterface()
        进入基建()
        when (conf.无人机房间类型) {
            "贸易站" -> 无人机加速贸易站(贸易站 = conf.无人机房间)
            "制造站" -> 无人机加速制造站(制造站 = conf.无人机房间)
        }
        收取基建产出()
    }

    private fun Device.收取基建产出() {
        tap(1203, 132).nap() //打开基建提醒（下方位置）
        repeat(3) { tap(239, 693).nap() } //收取贸易站产物、制造站产物和干员信赖
        tap(1136, 94).nap() //关闭基建提醒

        tap(1203, 92).nap() //打开基建提醒（一般位置）
        repeat(3) { tap(239, 693).nap() } //收取贸易站产物、制造站产物和干员信赖
        tap(1136, 94).nap() //关闭基建提醒
    }

    private fun Device.收取会客室() {
        // 进入会客室
        tap(1047, 234, desc = "进入会客室").sleepl()
        tap(414, 617, desc = "进入详情").sleep()

        // 关闭线索交流完毕的提示
        if (match(会客室_线索交流活动完毕)) {
            back().nap()
        }

        // 领取线索
        tap(1196, 180, desc = "进入收集线索").sleep()
        tap(805, 574, desc = "领取会客室线索").sleep()
        tap(986, 100, desc = "返回详情界面").sleep()

        // 接收线索
        tap(1199, 288, desc = "进入接收线索").sleep()
        tap(1074, 687, desc = "收取好友线索").sleep()
        tap(810, 396, desc = "返回详情界面").nap()

        // 传递线索
        tap(1197, 388, desc = "进入传递线索").sleep()
        whileNotMatch(自有库无线索) {
            tap(74, 183, desc = "选择第一个线索").nap()
            tap(1194, 135, desc = "传递给第一个好友").sleep()
        }
        tap(1244, 34, desc = "返回详情界面").sleep()

        // 开启线索交流（如果能）
        tap(389, 228, desc = "一号线索").nap()
        if (match(会客室_存在可用线索)) tap(930, 227, desc = "装入第一个线索").sleep()
        tap(987, 86, desc = "二号线索").nap()
        if (match(会客室_存在可用线索)) tap(930, 227, desc = "装入第一个线索").sleep()
        tap(1036, 86, desc = "三号线索").nap()
        if (match(会客室_存在可用线索)) tap(930, 227, desc = "装入第一个线索").sleep()
        tap(1092, 86, desc = "四号线索").nap()
        if (match(会客室_存在可用线索)) tap(930, 227, desc = "装入第一个线索").sleep()
        tap(1138, 86, desc = "五号线索").nap()
        if (match(会客室_存在可用线索)) tap(930, 227, desc = "装入第一个线索").sleep()
        tap(1185, 86, desc = "六号线索").nap()
        if (match(会客室_存在可用线索)) tap(930, 227, desc = "装入第一个线索").sleep()
        tap(1243, 86, desc = "七号线索").nap()
        if (match(会客室_存在可用线索)) tap(930, 227, desc = "装入第一个线索").sleep()
        tap(810, 396, desc = "返回详情界面").nap()
        tap(687, 650, desc = "解锁线索（如果能）").sleep()

        whileNotMatch(基建界面) {
            back(description = "返回基建界面").sleepl()
        }
    }

    private fun Device.无人机加速贸易站(贸易站: Pos) {
        tap(贸易站, desc = "默认贸易站").sleep()
        tap(79, 611, desc = "贵金属订单").sleep()
        whileMatch(贸易站_存在订单) {
            tap(290, 160, desc = "收获订单").sleep()
        }

        tap(373, 494, desc = "无人机加速").nap()
        tap(962, 335, desc = "最多").nap()
        tap(935, 585, desc = "确定").sleep()
        whileMatch(贸易站_存在订单) {
            tap(290, 160, desc = "收获订单").sleep()

            tap(373, 494, desc = "无人机加速").nap()
            tap(962, 335, desc = "最多").nap()
            tap(935, 585, desc = "确定").sleep()
        }

        whileNotMatch(基建界面) {
            back(description = "返回基建界面").sleep()
        }
    }

    private fun Device.无人机加速制造站(制造站: Pos) {
        tap(制造站, desc = "默认制造站").sleep()
        tap(制造站, desc = "默认制造站（防止变为收取）").sleep()
        tap(82, 610, desc = "制造详情").sleep()
        tap(1219, 536, desc = "无人机加速").nap()
        tap(962, 335, desc = "最多").nap()
        tap(935, 585, desc = "确定").sleep()
        tap(1125, 639, desc = "收取").sleep()

        whileNotMatch(基建界面) {
            back(description = "返回基建界面").sleep()
        }
    }

    private fun 换班() = device.apply {
        tap(74, 118) //进驻总览
        await(进驻总览界面)

        dragv(1200, 415, 0, -192, speed = 0.15).nap() //一个房间距离
        换班房间(Pos(670, 200), "1F02") //第一个房间

        repeat(3) { level ->
            dragv(1200, 415, 0, -245, speed = 0.15) //一层距离
            repeat(3) { room ->
                换班房间(Pos(670, 200), "B${level + 1}0${room + 1}") //贸易站、制造站或发电站
                dragv(1200, 415, 0, -192, speed = 0.15).nap() //一个房间距离
            }
            // 只有第一、二、三层有辅助设施
            if (level + 1 <= 3) {
                dragv(1200, 415, 0, -192, speed = 0.15).nap() //一个房间距离
            }
            // 只有第二层的办公室需要换班
            if (level + 1 == 2) {
                换班房间(Pos(670, 200), "B${level + 1}05") //辅助设施
            }
        }

        swipe(1200, 415, 1200, 415 + 2048, 10.0).nap()
        swipe(1200, 415, 1200, 415 + 2048, 10.0).nap()

        //前两个宿舍
        dragv(1200, 415, 0, -880, speed = 0.15).nap() //一个宿舍距离
        换班房间(Pos(670, 200), "B104", init = true) //宿舍
        dragv(1200, 415, 0, -880, speed = 0.15).nap() //一个宿舍距离
        换班房间(Pos(670, 200), "B204") //宿舍
        //后两个宿舍
        dragv(1200, 415, 0, -880, speed = 0.15).nap() //一个宿舍距离
        换班房间(Pos(670, 240), "B304") //第三个宿舍
        换班房间(Pos(670, 600), "B404") //第四个宿舍
    }

    private fun 换班房间(pos: Pos, room: String, init: Boolean = false) = device.apply {
        tap(pos)
        await(干员选择界面)

        if (init) {
            tap(1180, 40, desc = "").nap()
            whileNotMatch(心情增序) {
                tap(605, 170).nap()
            }
            whileNotMatch(未进驻筛选) {
                tap(430, 360).nap()
            }
            tap(950, 550).nap()
        }

        val shiftingOperatorsCount = when {
            room == "1F01" -> 5
            room == "1F02" -> 2
            room.endsWith("1") || room.endsWith("2") -> 3
            room.endsWith("3") -> 1
            room.endsWith("4") -> 5
            room.endsWith("5") -> 1
            else -> 5
        }
        for (num in 0 until shiftingOperatorsCount * 2) {
            tap(Pos(480 + (num / 2) * 144, 210 + (num % 2) * 300))
        }

        tap(1180, 675).nap() //确认
        whileNotMatch(进驻总览界面) {
            tap(1180, 675).nap() //确认
        }
    }

}
