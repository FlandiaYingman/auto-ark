package top.anagke.auto_ark

import top.anagke.auto_android.AutoAndroid
import top.anagke.auto_android.AutoInterruptedException
import top.anagke.auto_android.AutoModule
import top.anagke.auto_android.device.Device
import top.anagke.auto_android.device.which
import top.anagke.auto_ark.login.ArkLogin
import top.anagke.auto_ark.mail.ArkMail
import top.anagke.auto_ark.mission.ArkMission
import top.anagke.auto_ark.operate.ArkOperate
import top.anagke.auto_ark.recruit.ArkRecruit
import top.anagke.auto_ark.riic.ArkRIIC
import top.anagke.auto_ark.store.ArkStore
import top.anagke.auto_ark.update.ArkUpdate
import kotlin.io.path.Path

class AutoArk(
    val config: AutoArkConfig,
    var savedata: AutoArkSavedata,
    device: Device
) : AutoAndroid<AutoArk>(device) {

    override val name: String = "自动方舟"

    override val initModules: List<AutoModule<AutoArk>> = listOf(
        ArkUpdate(this),
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
        ArkOperate(this),
        ArkRecruit(this),
        ArkRIIC(this),
        ArkStore(this),
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