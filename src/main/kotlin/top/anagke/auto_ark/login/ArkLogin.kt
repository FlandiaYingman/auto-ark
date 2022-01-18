package top.anagke.auto_ark.login

import mu.KotlinLogging
import top.anagke.auto_android.*
import top.anagke.auto_android.util.minutes
import top.anagke.auto_ark.*

val logger = KotlinLogging.logger {}

class ArkLogin(
    private val device: Device,
    private val config: AutoArkConfig,
) : AutoModule {

    companion object {
        // 登录界面
        private val 开始界面 by tmpl(diff = 0.01)
    }

    override fun run(): Unit = device.run {
        logger.info { "登录明日方舟" }
        if (config.forceLogin) {
            loginForce()
        } else when {
            match(主界面) -> loginMainScreen()
            match(可跳出) -> loginJumpOut()
            else -> loginElse()
        }
    }


    private fun Device.loginForce() {
        logger.info { "登录明日方舟，强制登录，启动明日方舟中" }
        stop(config.server.activity)
        launch(config.server.activity)
        when (config.server) {
            ArkServer.OFFICIAL -> loginOfficial()
            ArkServer.BILIBILI -> loginBilibili()
        }
    }

    private fun Device.loginElse() {
        logger.info { "登录明日方舟，启动明日方舟中" }
        stop(config.server.activity)
        launch(config.server.activity)
        when (config.server) {
            ArkServer.OFFICIAL -> loginOfficial()
            ArkServer.BILIBILI -> loginBilibili()
        }
    }

    private fun Device.loginMainScreen() {
        await(主界面)
        logger.info { "登录明日方舟，已在主界面，完成登录" }
    }

    private fun Device.loginJumpOut() {
        logger.info { "登录明日方舟，可返回主界面，返回" }
        jumpOut()

        await(主界面)
        logger.info { "登录明日方舟，已返回主界面，完成登录" }
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
