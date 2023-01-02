package top.anagke.auto_ark.mail

import top.anagke.auto_android.device.assert
import top.anagke.auto_android.device.match
import top.anagke.auto_android.device.sleepl
import top.anagke.auto_android.img.Tmpl
import top.anagke.auto_ark.*

/** 收取未收取的邮件。 */
class ArkMail(
    auto: AutoArk,
) : ArkModule(auto) {

    companion object {
        private val 存在未领取邮件: Tmpl by tmpl()

        @JvmStatic
        fun main(args: Array<String>) {
            ArkMail(App.defaultAutoArk()).run()
        }
    }

    override val name: String = "邮件模块"

    override fun run(): Unit = device.run {
        assert(主界面)
        if (match(存在未领取邮件)) {
            tap(195, 35, "邮件").sleepl()
            tap(1145, 665, "收取所有邮件").sleepl()

            resetInterface()
        }
    }

}