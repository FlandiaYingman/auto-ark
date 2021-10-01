@file:Suppress("SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection",
    "SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection",
    "SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection",
    "SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection",
    "SpellCheckingInspection")

package top.anagke.auto_ark.ark.login

import mu.KotlinLogging
import top.anagke.auto_ark.adb.AndroidActivity
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.match
import top.anagke.auto_ark.adb.nap
import top.anagke.auto_ark.adb.whileNotMatch
import top.anagke.auto_ark.ark.atMainScreen
import top.anagke.auto_ark.ark.canJumpOut
import top.anagke.auto_ark.ark.hardJumpOut
import top.anagke.auto_ark.ark.jumpOut
import top.anagke.auto_ark.ark.template

class ArkLogin(
    private val device: Device,
) {

    companion object {
        val log = KotlinLogging.logger {}
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
        val focusedActivity = this.focusedActivity
        if (focusedActivity != arkActivity) {
            if (focusedActivity != null) stop(focusedActivity)
            launch(arkActivity)
            login()
        } else {
            when {
                match(atMainScreen) -> {
                }
                match(canJumpOut) -> {
                    jumpOut()
                }
                else -> {
                    stop(focusedActivity)
                    launch(arkActivity)
                    login()
                }
            }
        }
    }

    private fun login() = device.apply {
        log.info { "登录明日方舟" }
        whileNotMatch(atLoginScreen) {
            tap(640, 360).nap()
        }

        log.info { "检测到登录界面，登录" }
        tap(639, 507).nap() // 开始唤醒

        log.info { "等待登录完成" }
        hardJumpOut()
        hardJumpOut()

        log.info { "登录完成" }
    }

}