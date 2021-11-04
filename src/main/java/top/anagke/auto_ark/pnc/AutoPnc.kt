package top.anagke.auto_ark.pnc

import mu.KotlinLogging
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.ark.login.ArkLogin
import top.anagke.auto_ark.ark.mission.ArkMission
import top.anagke.auto_ark.ark.operate.ArkOperate
import top.anagke.auto_ark.ark.recruit.ArkRecruit
import top.anagke.auto_ark.ark.riic.ArkRiic
import top.anagke.auto_ark.ark.store.ArkStore
import java.time.DayOfWeek
import java.time.LocalDateTime

class AutoPnc(
    val config: AutoPncConfig,
    val device: Device = config.emulator.open(),
) {

    companion object {

        val log = KotlinLogging.logger {}

        val arkToday: DayOfWeek get() = LocalDateTime.now().minusHours(4).dayOfWeek

    }

    fun routine() {
        autoLogin()
        autoRecruit()
        autoOperate()
        autoRiic()
        //TODO: test, and do
        //autoStore()
        autoMission()
    }


    fun autoLogin() = runModule {
        ArkLogin(device).auto()
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
    }

}

fun main() {
    AutoPnc(appConfig).routine()
}