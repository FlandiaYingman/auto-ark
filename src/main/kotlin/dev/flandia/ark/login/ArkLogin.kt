package dev.flandia.ark.login

import org.tinylog.kotlin.Logger
import dev.flandia.android.device.*
import dev.flandia.android.util.minutes
import dev.flandia.android.util.seconds
import dev.flandia.ark.*

class ArkLogin(
    auto: AutoArk
) : ArkModule(auto) {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ArkLogin(App.defaultAutoArk()).run()
        }
    }

    private val conf = config.登录配置

    override val name: String = "登录模块"

    override fun run() = device.run {
        Logger.info("登录明日方舟")
        launch()
        when (config.服务器) {
            ArkServer.OFFICIAL -> loginOfficial()
        }
    }

    private fun Device.launch() {
        Logger.info("登录明日方舟，启动明日方舟中")
        stop(config.服务器.activity)
        launch(config.服务器.activity)
    }

    private fun Device.loginOfficial() {
        Logger.info("登录明日方舟（官服）")
        whileNotMatch(开始界面, 开始界面_第一次登录, timeout = 10.minutes) {
            tap(640, 360).nap()
        }
        val 第一次登录 = matched(开始界面_第一次登录)

        tap(640, 515, desc = "开始唤醒").sleepl()

        if (conf.用户名 != "") {
            Logger.info("登录明日方舟（官服），切换账号为：${conf.用户名}")

            if (第一次登录) {
                tap(640, 515, desc = "账号登录").sleep()
            } else {
                tap(925, 684, desc = "账号管理").nap()
                tap(640, 500, desc = "登录其它账号").nap()
            }

            tap(789, 560, desc = "密码登录").nap()

            tap(500, 270, desc = "用户名栏").nap()
            input(conf.用户名, desc = "输入用户名").nap()

            tap(500, 350, desc = "密码栏").nap()
            inputSecret(conf.密码, desc = "输入密码").nap()

            tap(472, 410, desc = "已同意……协议和政策").nap()

            Logger.info("登录明日方舟（官服），完成切换账号，登录")
            tap(640, 500, desc = "登录其它账号").sleepl()
        } else {
            if (第一次登录) {
                throw IllegalStateException("第一次登录，请手动完成登录或配置登陆配置")
            }
            Logger.info("登录明日方舟（官服），检测到登录界面，登录")
            tap(640, 515, desc = "开始唤醒").sleepl()
        }

        Logger.info("登录明日方舟（官服），等待登录完成")
        delay(10.seconds)
        whileNotMatch(主界面) {
            back().nap()
            tap(130, 489).nap() //防止卡在返回界面
        }
        resetInterface()

        await(主界面)
        Logger.info("登录明日方舟（官服），完成登录")
    }

}
