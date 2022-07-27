package top.anagke.auto_ark.login

import org.tinylog.kotlin.Logger
import top.anagke.auto_android.device.*
import top.anagke.auto_android.util.minutes
import top.anagke.auto_ark.*

class ArkLogin(
    auto: AutoArk
) : ArkModule(auto) {

    companion object;

    override val name: String = "登录模块"

    override fun run() = device.run {
        Logger.info("登录明日方舟")
        launch()
        when (config.server) {
            ArkServer.OFFICIAL -> loginOfficial()
            ArkServer.BILIBILI -> loginBilibili()
        }
    }

    private fun Device.launch() {
        Logger.info("登录明日方舟，启动明日方舟中")
        stop(config.server.activity)
        launch(config.server.activity)
    }

    private fun Device.loginOfficial() {
        Logger.info("登录明日方舟（官服）")
        whileNotMatch(开始界面, timeout = 10.minutes) {
            tap(640, 360).nap()
        }

        Logger.info("登录明日方舟（官服），检测到登录界面，登录")
        tap(639, 507).nap() // 开始唤醒

        Logger.info("登录明日方舟（官服），等待登录完成")
        whileNotMatch(主界面) {
            back().nap()
            tap(130, 489).nap() //防止卡在返回界面
        }
        resetInterface()

        await(主界面)
        Logger.info("登录明日方舟（官服），完成登录")
    }

    private fun Device.loginBilibili() {
        Logger.info("登录明日方舟（B服）")
        delay(5000)
        whileNotMatch(主界面, timeout = 10.minutes) {
            back().nap()
            tap(130, 489).nap() //防止卡在返回界面
        }
        resetInterface()

        await(主界面)
        Logger.info("登录明日方舟（B服），完成登录")
    }

}
