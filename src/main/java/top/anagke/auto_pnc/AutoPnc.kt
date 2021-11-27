package top.anagke.auto_pnc

import mu.KotlinLogging
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_pnc.factory.PncFactory
import top.anagke.auto_pnc.login.PncLogin
import top.anagke.auto_pnc.oasis.PncOasis

class AutoPnc(
    val config: AutoPncConfig,
    val device: Device = config.emulator.open(),
) {

    companion object {
        val log = KotlinLogging.logger {}
    }

    fun routine() {
        autoLogin()
        autoOasis()
        autoFactory()
    }

    fun autoLogin() {
        PncLogin(device, config).auto()
    }

    fun autoOasis() {
        PncOasis(device).auto()
    }

    fun autoFactory() {
        PncFactory(device).auto()
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
    AutoPnc(pncConfig).routine()
}