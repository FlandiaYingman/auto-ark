package top.anagke.auto_ark.ark

import kotlinx.serialization.Serializable
import mu.KotlinLogging
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.delay
import top.anagke.auto_ark.adb.matched
import top.anagke.auto_ark.adb.nap
import top.anagke.auto_ark.adb.notMatch

private val log = KotlinLogging.logger { }


// 欢迎界面
private val atSplashScreen = template("login/atSplashScreen.png", diff = 0.04)
// 登录界面
private val atLoginScreen = template("login/atLoginScreen.png", diff = 0.01)
// 弹出日常公告
private val popupDailyAnnounce = template("login/popupDailyAnnounce.png", diff = 0.01)
// 弹出日常奖励
private val popupDailyBonus = template("login/popupDailyBonus.png", diff = 0.10)


@Serializable
data class LoginConfig(
    val loginType: LoginType = LoginType.OFFICIAL,
    val username: String = "<username>",
    val password: String = "<password>",
) {
    enum class LoginType {
        OFFICIAL,
        BILIBILI;
    }
}


fun Device.login() {
    val loginConfig = appConfig.loginConfig
    when (loginConfig.loginType) {
        LoginConfig.LoginType.OFFICIAL -> loginOfficial(loginConfig.username, loginConfig.password)
        LoginConfig.LoginType.BILIBILI -> loginBilibili()
    }
}

/**
 * 登录明日方舟。
 *
 * 由于这通常是最先调用的操作，因此其在开始于不符合的界面时不会报错而等待。
 *
 * 开始于：登录界面或欢迎界面。
 * 结束于：主界面。
 */
fun Device.loginOfficial(username: String, password: String) {
    log.info { "登录明日方舟" }
    await(atLoginScreen, atSplashScreen)
    if (matched(atSplashScreen)) {
        log.info { "检测到欢迎界面，跳过" }
        tap(640, 360).nap()
        await(atLoginScreen)
    }

    log.info { "检测到登录界面，输入登录信息" }
    tap(923, 683).nap() // 账号管理
    tap(412, 509).delay(1000)  // 账号登录

    tap(509, 429).nap() // 账号
    input(username).nap() // *输入账号*
    tap(1199, 669).nap() // *完成输入*
    tap(512, 482).nap() // 密码
    input(password).nap() //*输入密码*
    tap(1199, 669).nap() // *完成输入*

    tap(639, 577).nap() //登录

    log.info { "登录信息输入完毕，等待登录完成" }
    await(atMainScreen, popupDailyAnnounce, popupDailyBonus)
    delay(10000) //等待日常公告/奖励弹出
    jumpOut()
    log.info { "登录完成" }
}

/**
 * 登录明日方舟（B服）。
 *
 * 由于这通常是最先调用的操作，因此其在开始于不符合的界面时不会报错而等待。
 *
 * 开始于：登录界面或欢迎界面。
 * 结束于：主界面。
 */
fun Device.loginBilibili() {
    log.info { "登录明日方舟（B服）" }
    await(atSplashScreen, atMainScreen)
    if (matched(atSplashScreen)) {
        log.info { "检测到欢迎界面，跳过" }
        tap(640, 360).nap()
        await(atMainScreen)
    }
    await(atMainScreen, popupDailyAnnounce, popupDailyBonus)
    delay(2500) //等待日常公告/奖励弹出
    while (notMatch(atMainScreen)) {
        back()
    }
    log.info { "登录完成" }
}