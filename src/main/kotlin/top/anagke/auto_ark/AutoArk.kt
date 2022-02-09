package top.anagke.auto_ark

import top.anagke.auto_android.AutoAndroid
import top.anagke.auto_android.AutoModule
import top.anagke.auto_android.device.BlueStacks
import top.anagke.auto_android.device.Device
import top.anagke.auto_android.device.match
import top.anagke.auto_ark.login.ArkLogin
import top.anagke.auto_ark.mission.ArkMission
import top.anagke.auto_ark.operate.ArkOperate
import top.anagke.auto_ark.recruit.ArkRecruit
import top.anagke.auto_ark.riic.ArkRiic
import top.anagke.auto_ark.store.ArkStore
import top.anagke.auto_ark.update.ArkUpdate

class AutoArk(
    val config: AutoArkConfig,
    var cache: AutoArkCache,
    device: Device
) : AutoAndroid<AutoArk>(device) {

    companion object {
        fun default(): AutoArk {
            val config = AutoArkConfig.loadConfig()
            val cache = AutoArkCache.loadCache(config.cacheLocation)
            return AutoArk(config, cache, BlueStacks(config.emulator).launch().device)
        }
    }

    override val name: String = "自动方舟"

    override val initModules: List<AutoModule<AutoArk>> = listOf(
        ArkUpdate(this),
        ArkLogin(this),
    )

    override val workModules: List<AutoModule<AutoArk>> = listOf(
        ArkOperate(this),
        ArkRiic(this),
        ArkRecruit(this),
        ArkStore(this),
        ArkMission(this),
    )

    override val finalModules: List<AutoModule<AutoArk>> = listOf(
        createModule("清理模块") {
            device.stop(config.server.activity, description = "停止明日方舟")
        },
    )

    override fun isAtMain(): Boolean = device.match(主界面)

    override fun returnToMain() = device.jumpOut()


    override fun beforeModule() {
        AutoArkCache.saveCache(config.cacheLocation, cache)
    }

    override fun afterModule() {
        AutoArkCache.saveCache(config.cacheLocation, cache)
    }

}