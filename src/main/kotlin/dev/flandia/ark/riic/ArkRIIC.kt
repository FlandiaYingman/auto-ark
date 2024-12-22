package dev.flandia.ark.riic

import dev.flandia.android.device.*
import dev.flandia.ark.*

class ArkRIIC(
    auto: AutoArk,
) : ArkModule(auto) {

    companion object {
        private val 基建界面 by tmpl()
        private val 自有库无线索 by tmpl()
        private val 会客室_存在可用线索 by tmpl()
        private val 会客室_线索交流活动完毕 by tmpl()

        @JvmStatic
        fun main(args: Array<String>) {
            ArkRIIC(App.defaultAutoArk()).run()
        }
    }

    override val name: String = "基建模块"

    override fun run(): Unit = device.run {
        assert(主界面)

        进入基建()
        收取基建()
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
    }

    private fun Device.收取基建产出() {
        tap(1203, 132).nap() //打开基建提醒（下方位置）
        repeat(4) { tap(239, 693).sleep() } //收取贸易站产物、制造站产物和干员信赖
        tap(1136, 94).nap() //关闭基建提醒

        tap(1203, 92).nap() //打开基建提醒（一般位置）
        repeat(4) { tap(239, 693).sleep() } //收取贸易站产物、制造站产物和干员信赖
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

}
