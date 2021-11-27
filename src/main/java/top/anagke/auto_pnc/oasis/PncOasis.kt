package top.anagke.auto_pnc.oasis

import top.anagke.auto_android.img.Tmpl
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.assert
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.match
import top.anagke.auto_ark.adb.nap
import top.anagke.auto_pnc.jumpOut
import top.anagke.auto_pnc.template
import top.anagke.auto_pnc.主界面

class PncOasis(
    private val device: Device,
) {

    companion object {
        private val 绿洲界面: Tmpl = template("oasis/绿洲界面.png")
        private val 绿洲界面_可收取: Tmpl = template("oasis/绿洲界面_可收取.png")

        @JvmStatic
        fun main(args: Array<String>) {
            PncOasis(Device()).auto()
        }
    }

    fun auto() {
        enterOasis()
        collectResources()
        exitOasis()
    }

    private fun enterOasis() {
        device.assert(主界面)
        device.tap(928, 417) //绿洲
        device.await(绿洲界面)
    }

    private fun collectResources() {
        if (device.match(绿洲界面_可收取)) {
            device.tap(84, 565).nap() //收取资源
            device.tap(84, 565).nap() //收取资源
        }
    }

    private fun exitOasis() {
        device.assert(绿洲界面)
        device.jumpOut()
    }

}