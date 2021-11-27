package top.anagke.auto_pnc.factory

import top.anagke.auto_android.img.Tmpl
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.assert
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.nap
import top.anagke.auto_ark.adb.sleep
import top.anagke.auto_ark.adb.whileMatch
import top.anagke.auto_pnc.jumpOut
import top.anagke.auto_pnc.template
import top.anagke.auto_pnc.主界面

class PncFactory(
    private val device: Device,
) {

    companion object {
        private val 加工厂界面: Tmpl = template("factory/加工厂界面.png")
        private val 加工厂界面_可领取: Tmpl = template("factory/加工厂界面_可领取.png")
        private val 加工厂界面_可加速: Tmpl = template("factory/加工厂界面_可加速.png")

        @JvmStatic
        fun main(args: Array<String>) {
            PncFactory(Device()).auto()
        }
    }

    fun auto() {
        enterFactory()
        collectTasks()
        submitTasks()
        exitFactory()
    }

    private fun enterFactory() {
        device.assert(主界面)
        device.tap(1166, 595) //加工厂
        device.await(加工厂界面)
    }

    private fun collectTasks() {
        device.tap(127, 652).nap()
        device.whileMatch(加工厂界面_可领取) {
            device.tap(941, 178).sleep() //收取资源
            device.tap(1049, 95).nap() //确认收取
        }
        device.tap(1049, 95).nap()
    }

    private fun submitTasks() {
        device.tap(497, 185).nap() //采掘矿场
        device.drag(905, 639, 908, 184).nap()
        device.tap(1062, 415).nap() //中模三角数据
        device.tap(762, 565).nap() //最多
        device.tap(723, 639).nap() //确认
        device.doubleTap(390, 691).nap() //确保返回到加工厂界面

        device.tap(789, 186).nap() //物资车间
        device.tap(1048, 294).nap() //作战经验*600
        device.tap(762, 565).nap() //最多
        device.tap(723, 639).nap() //确认
        device.doubleTap(390, 691).nap() //确保返回到加工厂界面


        device.tap(350, 357).nap() //礼品工房
        device.tap(1048, 294).nap() //快餐
        device.tap(762, 565).nap() //最多
        device.tap(723, 639).nap() //确认
        device.doubleTap(390, 691).nap() //确保返回到加工厂界面


        device.tap(615, 366).nap() //数据封装中心
        device.drag(1060, 600, 1060, 0).nap() //划到最下
        device.tap(1048, 529).nap() //技能枢核
        device.tap(762, 565).nap() //最多
        device.tap(723, 639).nap() //确认
        device.doubleTap(390, 691).nap() //确保返回到加工厂界面

    }

    private fun exitFactory() {
        device.assert(加工厂界面)
        device.jumpOut()
    }

}