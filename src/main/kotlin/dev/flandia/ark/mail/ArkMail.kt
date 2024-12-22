package dev.flandia.ark.mail

import dev.flandia.android.device.assert
import dev.flandia.android.device.match
import dev.flandia.android.device.sleepl
import dev.flandia.ark.*

/** 收取未收取的邮件。 */
class ArkMail(
    auto: AutoArk,
) : ArkModule(auto) {

    companion object {
        private val 存在未领取邮件 by tmpl()

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