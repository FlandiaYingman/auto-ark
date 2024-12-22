package dev.flandia.ark

import dev.flandia.android.AutoAndroid
import dev.flandia.android.AutoInterruptedException
import dev.flandia.android.AutoModule
import dev.flandia.android.device.Device
import dev.flandia.android.device.which
import dev.flandia.ark.login.ArkLogin
import dev.flandia.ark.mail.ArkMail
import dev.flandia.ark.mission.ArkMission
import dev.flandia.ark.operate.ArkOperate
import dev.flandia.ark.recruit.ArkRecruit
import dev.flandia.ark.riic.ArkRIIC
import dev.flandia.ark.store.ArkStore
import dev.flandia.ark.update.ArkUpdate
import kotlin.io.path.Path

class AutoArk(
    val config: AutoArkConfig,
    var savedata: AutoArkSavedata,
    device: Device
) : AutoAndroid<AutoArk>(device) {

    override val name: String = "自动方舟"

    override val initModules: List<AutoModule<AutoArk>> = listOf(
        dev.flandia.ark.update.ArkUpdate(this),
        createModule("首次运行模块") {
            if (savedata.isFirstRun) {
                savedata.isFirstRun = false
                AutoArkSavedata.saveSaveData(Path(App.SAVEDATA_PATH), savedata)
                throw Error("第一次运行，初始化并退出。")
            }
        },
        ArkLogin(this),
    )

    override val workModules: List<AutoModule<AutoArk>> = listOf(
        ArkMail(this),
        ArkRIIC(this),
        ArkStore(this),
        ArkRecruit(this),
        ArkOperate(this),
        ArkMission(this),
    )

    override val finalModules: List<AutoModule<AutoArk>> = listOf(
        createModule("清理模块") {
            device.stop(ArkServer.OFFICIAL.activity, desc = "停止明日方舟")
        },
    )

    override fun isInterfaceAtMain(): Boolean {
        return device.which(主界面, 登录认证失效).also {
            if (it == 登录认证失效) throw AutoInterruptedException("登录认证失效")
        } == 主界面
    }

    override fun setInterfaceToMain() = device.resetInterface()


    override fun beforeModule() {
        AutoArkSavedata.saveSaveData(Path(App.SAVEDATA_PATH), savedata)
    }

    override fun afterModule() {
        AutoArkSavedata.saveSaveData(Path(App.SAVEDATA_PATH), savedata)
    }

}