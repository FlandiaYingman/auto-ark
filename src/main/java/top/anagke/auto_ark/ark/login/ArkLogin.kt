package top.anagke.auto_ark.ark.login

import mu.KotlinLogging
import top.anagke.auto_ark.adb.AndroidActivity
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.match
import top.anagke.auto_ark.adb.nap
import top.anagke.auto_ark.adb.whileNotMatch
import top.anagke.auto_ark.ark.atMainScreen
import top.anagke.auto_ark.ark.canJumpOut
import top.anagke.auto_ark.ark.jumpOut
import top.anagke.auto_ark.ark.template
import top.anagke.auto_ark.util.minutes

class ArkLogin(
    private val device: Device,
) {

    companion object {
        val logger = KotlinLogging.logger {}
        // 登录界面
        private val atLoginScreen = template("login/atLoginScreen.png", diff = 0.01)

        private val arkActivity = AndroidActivity(
            "com.hypergryph.arknights",
            "com.u8.sdk.U8UnityContext"
        )
    }

    fun auto() {
        launch()
    }

    private fun launch() = device.apply {
        logger.info { "启动明日方舟" }
        if (focusedActivity == arkActivity) {
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
        stop(arkActivity)
        launch(arkActivity)
        login()
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

}