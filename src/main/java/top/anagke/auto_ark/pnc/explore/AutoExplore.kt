package top.anagke.auto_ark.pnc.explore

import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.assert
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.nap
import top.anagke.auto_ark.adb.sleep
import top.anagke.auto_ark.adb.whileNotMatch
import top.anagke.auto_ark.pnc.atMainScreen
import top.anagke.auto_ark.pnc.canJumpOut
import top.anagke.auto_ark.pnc.template
import top.anagke.auto_ark.util.minutes

class AutoExplore(
    val device: Device,
) {

    companion object {

        private val canFarmAI = template("explore/canFarmAI.png")
        private val atPrepareScreen = template("explore/atPrepareScreen.png")
        private val canAI = template("explore/canAI.png")
        private val 作战成功 = template("explore/作战成功.png", diff = 0.02)

    }

    fun auto() {

    }

    fun explore() = device.apply {
        assert(atMainScreen)
        tap(1085, 230) //探索
        await(canJumpOut)
    }

    fun farmFragments() = device.apply {
        explore()
        tap(610, 175) //碎片搜索

    }

    fun farmAI() = device.apply {
        await(canFarmAI)
        tap(1127, 636) //准备作战

        await(atPrepareScreen)
        tap(1127, 636) //作战开始

        whileNotMatch(canAI) {
            tap(200, 550).nap() //开启计划模式
        }
        tap(326, 547) //执行计划

        await(作战成功, timeout = 5.minutes)
        tap(1052, 623) //返回
        sleep()
    }

}

fun AutoExplore.farm海域探源() {
    while (true) {
        device.tap(837, 307).nap()
        farmAI()
    }
}

fun main() {
    AutoExplore(Device()).farm海域探源()
}