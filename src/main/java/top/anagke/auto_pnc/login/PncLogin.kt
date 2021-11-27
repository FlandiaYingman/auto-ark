package top.anagke.auto_pnc.login

import top.anagke.auto_android.img.Tmpl
import top.anagke.auto_ark.adb.AndroidActivity
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.find
import top.anagke.auto_ark.adb.nap
import top.anagke.auto_ark.adb.whileNotMatch
import top.anagke.auto_pnc.AutoPncConfig
import top.anagke.auto_pnc.template
import top.anagke.auto_pnc.主界面

class PncLogin(
    private val device: Device,
    private val config: AutoPncConfig,
) {

    companion object {
        private val PNC_ACTIVITY = AndroidActivity(
            "com.sunborn.neuralcloud.cn",
            "com.mica.micasdk.ui.FoundationActivity",
        )

        private val 登录界面: Tmpl = template("登陆界面.png")
        private val 主界面_可退出: Tmpl = template("主界面_可退出.png", diff = 0.15)
    }

    fun auto() {
        launchPnc()
        login()
    }

    private fun launchPnc() {
        device.stop(PNC_ACTIVITY)
        device.launch(PNC_ACTIVITY)
    }

    private fun login() {
        device.await(登录界面)
        device.tap(1200, 95).nap() //切换用户

        device.doubleTap(570, 272).nap() //选择用户名
        device.input(config.username).nap()

        device.doubleTap(570, 336).nap() //选择密码
        device.input(config.password).nap()

        device.tap(826, 411) //登录
        device.whileNotMatch(主界面) {
            val pos = device.find(主界面_可退出)
            if (pos != null) device.tap(pos.x, pos.y).nap()
        }
    }

}