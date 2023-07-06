package top.anagke.auto_ark.login

import org.tinylog.kotlin.Logger
import top.anagke.auto_android.device.*
import top.anagke.auto_android.util.minutes
import top.anagke.auto_android.util.seconds
import top.anagke.auto_ark.*

class ArkLogin(
    auto: AutoArk
) : ArkModule(auto) {

    companion object {
        private val 登录界面 by tmpl()

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
        whileNotMatch(开始界面, timeout = 10.minutes) {
            tap(640, 360).nap()
        }

        if (conf.切换账户) {
            Logger.info("登录明日方舟（官服），切换账号为：${conf.用户名}")

            tap(925, 684, desc = "账号管理").nap()

            tap(640, 500, desc = "登录其它账号").nap()
            tap(784, 560, desc = "密码登录").nap()

            tap(500, 270, desc = "用户名栏").nap()
            input(conf.用户名, desc = "输入用户名").nap()

            tap(500, 350, desc = "密码栏").nap()
            inputSecret(conf.密码, desc = "输入密码").nap()

            tap(472, 410, desc = "已同意……协议和政策").nap()

            Logger.info("登录明日方舟（官服），完成切换账号，登录")
            tap(640, 500, desc = "登录其它账号").sleepl()
        } else {
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
