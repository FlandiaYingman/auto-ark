package top.anagke.auto_ark.login

import org.tinylog.kotlin.Logger
import top.anagke.auto_android.device.*
import top.anagke.auto_android.util.minutes
import top.anagke.auto_ark.*

class ArkLogin(
    auto: AutoArk
) : ArkModule(auto) {

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
        whileNotMatch(开始界面, timeout = 10.minutes) {
            tap(640, 360).nap()
        }

        if (conf.切换账户) {
            Logger.info("登录明日方舟（官服），切换账号为：${conf.用户名}")

            tap(923, 681, description = "账号管理").nap()
            tap(525, 508, description = "账号登录").sleep()

            tap(508, 430, description = "用户名栏").nap()
            input(conf.用户名, description = "输入用户名").nap()
            tap(1198, 668, description = "完成输入").nap()

            tap(508, 484, description = "密码栏").nap()
            inputSecret(conf.密码, description = "输入密码").nap()
            tap(1198, 668, description = "完成输入").nap()

            Logger.info("登录明日方舟（官服），完成切换账号，登录")
            tap(638, 578, description = "登录").sleepl()
        } else {
            Logger.info("登录明日方舟（官服），检测到登录界面，登录")
            tap(639, 507, description = "开始唤醒").sleepl()
        }

        Logger.info("登录明日方舟（官服），等待登录完成")
        whileNotMatch(主界面) {
            back().nap()
            tap(130, 489).nap() //防止卡在返回界面
        }
        resetInterface()

        await(主界面)
        Logger.info("登录明日方舟（官服），完成登录")
    }

}
