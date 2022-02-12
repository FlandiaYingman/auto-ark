package top.anagke.auto_ark.login

import mu.KotlinLogging
import top.anagke.auto_android.device.*
import top.anagke.auto_android.util.minutes
import top.anagke.auto_ark.*

class ArkLogin(
    auto: AutoArk
) : ArkModule(auto) {

    companion object {

        val logger = KotlinLogging.logger {}

        private val 开始界面 by tmpl(diff = 0.01)

    }

    override val name: String = "登录模块"

    override fun run() = device.run {
        logger.info { "登录明日方舟" }
        launch()
        when (config.server) {
            ArkServer.OFFICIAL -> loginOfficial()
            ArkServer.BILIBILI -> loginBilibili()
        }
    }

    private fun Device.launch() {
        logger.info { "登录明日方舟，启动明日方舟中" }
        stop(config.server.activity)
        launch(config.server.activity)
    }

    private fun Device.loginOfficial() {
        logger.info { "登录明日方舟（官服）" }
        whileNotMatch(开始界面, timeout = 10.minutes) {
            tap(640, 360).nap()
        }

        logger.info { "登录明日方舟（官服），检测到登录界面，登录" }
        tap(639, 507).nap() // 开始唤醒

        logger.info { "登录明日方舟（官服），等待登录完成" }
        whileNotMatch(主界面) {
            back().nap()
            tap(130, 489).nap() //防止卡在返回界面
        }
        jumpOut()

        await(主界面)
        logger.info { "登录明日方舟（官服），完成登录" }
    }

    private fun Device.loginBilibili() {
        logger.info { "登录明日方舟（B服）" }
        delay(5000)
        whileNotMatch(主界面) {
            back().nap()
            tap(130, 489).nap() //防止卡在返回界面
        }
        jumpOut()

        await(主界面)
        logger.info { "登录明日方舟（B服），完成登录" }
    }

}
