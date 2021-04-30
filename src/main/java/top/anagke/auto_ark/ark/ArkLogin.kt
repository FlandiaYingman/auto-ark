package top.anagke.auto_ark.ark

import kotlinx.serialization.Serializable
import top.anagke.auto_ark.adb.Ops
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.back
import top.anagke.auto_ark.adb.input
import top.anagke.auto_ark.adb.match
import top.anagke.auto_ark.adb.ops
import top.anagke.auto_ark.adb.tap

@Serializable
data class LoginProps(
    val username: String = "<username>",
    val password: String = "<password>",
)

private val props = arkProps.loginProps

// 欢迎界面
val awaitSplashScreen by template("awaitSplashScreen.png")
// 登录界面
val awaitLoginScreen by template("awaitLoginScreen.png")

val awaitDailyAnnouncement by template("awaitDailyAnnouncement.png", diff = 0.02)
val awaitDailyBonus by template("awaitDailyBonus.png", diff = 0.02)

/**
 * 登录明日方舟。
 *
 * 由于这通常是最先调用的操作，因此其在开始于不符合的界面时不会报错而等待。
 *
 * 开始于：欢迎界面或登录界面。
 * 结束于：主界面。
 */
fun login(): Ops {
    return ops {
        await(awaitLoginScreen, awaitSplashScreen).let {
            if (it == awaitSplashScreen) {
                tap(640, 360)
                await(awaitLoginScreen)
            }
        }
        tap(923, 683) // 账号管理
        tap(412, 509, delay = 1000)  // 账号登录
        tap(509, 429) // 账号
        input(props.username) // *输入账号*
        tap(1199, 669) // *完成输入*
        tap(512, 482) // 密码
        input(props.password) //*输入密码*
        tap(1199, 669) // *完成输入*
        tap(639, 577) //登录
        await(atMainScreen, awaitDailyAnnouncement, awaitDailyBonus).let {
            if (it == awaitDailyAnnouncement || it == awaitDailyBonus) {
                while (!match(atMainScreen)) {
                    back(delay = 500)
                }
            }
        }
    }
}