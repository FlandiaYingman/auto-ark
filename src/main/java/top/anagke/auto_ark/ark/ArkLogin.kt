package top.anagke.auto_ark.ark

import kotlinx.serialization.Serializable
import mu.KotlinLogging
import top.anagke.auto_ark.adb.Ops
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.back
import top.anagke.auto_ark.adb.delay
import top.anagke.auto_ark.adb.input
import top.anagke.auto_ark.adb.matched
import top.anagke.auto_ark.adb.notMatch
import top.anagke.auto_ark.adb.ops
import top.anagke.auto_ark.adb.tap

@Serializable
data class LoginProps(
    val username: String = "<username>",
    val password: String = "<password>",
)

private val props = arkProps.loginProps

private val log = KotlinLogging.logger { }


// 欢迎界面
private val atSplashScreen = template("login/atSplashScreen.png", diff = 0.04)
// 登录界面
private val atLoginScreen = template("login/atLoginScreen.png", diff = 0.01)
// 弹出日常公告
private val popupDailyAnnounce = template("login/popupDailyAnnounce.png", diff = 0.01)
//TODO To Be Tested
// 弹出日常奖励
private val popupDailyBonus = template("login/popupDailyBonus.png", diff = 0.02)


/**
 * 登录明日方舟。
 *
 * 由于这通常是最先调用的操作，因此其在开始于不符合的界面时不会报错而等待。
 *
 * 开始于：登录界面或欢迎界面。
 * 结束于：主界面。
 */
fun login(): Ops {
    return ops {
        log.info { "登录明日方舟" }

        log.info { "等待进入登录界面或欢迎界面" }
        await(atLoginScreen, atSplashScreen)
        if (matched(atSplashScreen)) {
            log.info { "检测到进入欢迎界面，进入登录界面" }

            tap(640, 360)
            await(atLoginScreen)
        }

        log.info { "检测到登录界面，输入登录信息" }
        tap(923, 683) // 账号管理
        tap(412, 509, delay = 1000)  // 账号登录

        tap(509, 429) // 账号
        input(props.username) // *输入账号*
        tap(1199, 669) // *完成输入*

        tap(512, 482) // 密码
        input(props.password) //*输入密码*
        tap(1199, 669) // *完成输入*
        tap(639, 577) //登录

        log.info { "登录信息输入完毕，等待登录完成" }
        await(atMainScreen, popupDailyAnnounce, popupDailyBonus)
        delay(2500) //等待日常公告/奖励弹出
        while (notMatch(atMainScreen)) {
            back(delay = 1000)
        }

        log.info { "登录完成" }
    }
}

/**
 * 登录明日方舟（Bilibili服务器）。
 *
 * 由于这通常是最先调用的操作，因此其在开始于不符合的界面时不会报错而等待。
 *
 * 开始于：登录界面或欢迎界面。
 * 结束于：主界面。
 */
fun loginBilibili(): Ops {
    return ops {
        log.info { "登录明日方舟（Bilibili服务器）" }

        log.info { "等待进入欢迎界面或主界面" }
        await(atSplashScreen, atMainScreen).let {
            if (it == atSplashScreen) {
                log.info { "检测到欢迎界面，进入主界面" }
                tap(640, 360)
            }
        }

        log.info { "等待进入主界面" }
        await(atMainScreen, popupDailyAnnounce, popupDailyBonus).let {
            delay(2500) //等待日常公告/奖励弹出
            while (notMatch(atMainScreen)) {
                back(delay = 1000)
            }
        }

        log.info { "检测到主界面，登录完成" }
    }
}