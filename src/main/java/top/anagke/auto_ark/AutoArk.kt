package top.anagke.auto_ark

import mu.KotlinLogging
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.login.ArkLogin
import top.anagke.auto_ark.mission.ArkMission
import top.anagke.auto_ark.operate.ArkOperate
import top.anagke.auto_ark.recruit.ArkRecruit
import top.anagke.auto_ark.riic.ArkRiic
import top.anagke.auto_ark.store.ArkStore
import java.time.DayOfWeek
import java.time.LocalDateTime

class AutoArk(
    val config: AutoArkConfig,
    val device: Device = config.emulator.open(),
) {

    companion object {

        val log = KotlinLogging.logger {}

        val arkToday: DayOfWeek get() = LocalDateTime.now().minusHours(4).dayOfWeek

    }

    fun routine() {
        autoUpdate()
        autoLogin()
        autoRecruit()
        autoOperate()
        autoRiic()
        autoStore()
        autoMission()
    }


    fun autoUpdate() = runModule {
        ArkUpdate(device, config).auto()
    }

    fun autoLogin() = runModule {
        ArkLogin(device, config).auto()
    }

    fun autoRecruit() = runModule {
        ArkRecruit(device, config.recruitConfig).auto()
    }

    fun autoOperate() = runModule {
        ArkOperate(device, config.operateConfig).auto()
    }

    fun autoRiic() = runModule {
        ArkRiic(device).auto()
    }

    fun autoStore() = runModule {
        ArkStore(device).auto()
    }

    fun autoMission() = runModule {
        ArkMission(device).auto()
    }


    private fun runModule(block: () -> Unit) {
        try {
            block()
            saveAppConfig(config)
        } catch (e: Exception) {
            onModuleError(e)
        } finally {
            onModuleEnds()
        }
    }

    private fun onModuleEnds() = device.apply {
        saveAppConfig(config)
    }

    private fun onModuleError(e: Exception) = device.apply {
        log.error("错误发生，尝试退出到主界面", e)
        jumpOut()
    }

}

fun main() {
    AutoArk(appConfig).routine()
}