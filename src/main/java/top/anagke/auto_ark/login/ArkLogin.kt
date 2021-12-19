package top.anagke.auto_ark.login

import mu.KotlinLogging
import top.anagke.auto_android.util.minutes
import top.anagke.auto_ark.AutoArkConfig
import top.anagke.auto_ark.adb.AndroidActivity
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.delay
import top.anagke.auto_ark.adb.match
import top.anagke.auto_ark.adb.nap
import top.anagke.auto_ark.adb.whileNotMatch
import top.anagke.auto_ark.atMainScreen
import top.anagke.auto_ark.canJumpOut
import top.anagke.auto_ark.jumpOut
import top.anagke.auto_ark.template

class ArkLogin(
    private val device: Device,
    private val config: AutoArkConfig,
) {

    companion object {
        val logger = KotlinLogging.logger {}
        // 登录界面
        private val atLoginScreen = template("login/atLoginScreen.png", diff = 0.01)

        private val arkActivity = AndroidActivity(
            "com.hypergryph.arknights",
            "com.u8.sdk.U8UnityContext"
        )
        private val arkBilibiliActivity = AndroidActivity(
            "com.hypergryph.arknights.bilibili",
            ""
        )
    }

    fun auto() {
        when {
            config.isBilibili -> {
                launchBilibili()
                loginBilibili()
            }
            else -> {
                launch()
                login()
            }
        }
    }

    private fun launch() = launch(arkActivity)
    private fun launchBilibili() = launch(arkBilibiliActivity)


    private fun launch(activity: AndroidActivity) = device.apply {
        logger.info { "启动明日方舟" }
        if (focusedActivity == arkBilibiliActivity && !config.forceLogin) {
            logger.info { "明日方舟已启动" }
            if (match(atMainScreen)) {
                logger.info { "明日方舟位于主界面" }
                return@apply
            }
            if (match(canJumpOut)) {
                logger.info { "明日方舟非位于主界面，可跳转到主界面，跳转" }
                jumpOut()
                return@apply
            }
            logger.info { "明日方舟无法跳转到主界面，尝试重新启动" }
        }
        stop(activity)
        launch(activity)
    }

    private fun login() = device.apply {
        logger.info { "登录明日方舟" }
        whileNotMatch(atLoginScreen, timeout = 5.minutes) {
            tap(640, 360).nap()
        }

        logger.info { "检测到登录界面，登录" }
        tap(639, 507).nap() // 开始唤醒

        logger.info { "等待登录完成" }
        whileNotMatch(atMainScreen) {
            back().nap()
            tap(130, 489).nap() //防止卡在返回界面
        }
        jumpOut()
        await(atMainScreen)

        logger.info { "登录完成" }
    }

    private fun loginBilibili() = device.apply {
        logger.info { "登录明日方舟（B服）" }
        delay(5000)
        whileNotMatch(atMainScreen) {
            back().nap()
            tap(130, 489).nap() //防止卡在返回界面
        }
        jumpOut()
        await(atMainScreen)
        logger.info { "登录完成" }
    }

}