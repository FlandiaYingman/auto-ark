package top.anagke.auto_ark

import org.tinylog.kotlin.Logger
import top.anagke.auto_android.device.BlueStacks
import top.anagke.auto_android.device.Device
import kotlin.io.path.Path
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

object App {

    const val CONFIG_PATH = "./config.yaml"
    const val SAVEDATA_PATH = "./savedata.yaml"


    fun run() {
        val (config, savedata) = loadConfigAndSavedata()
        BlueStacks(config.emulator).launch().use {
            AutoArk(config, savedata, it.device).doRoutine()
        }
    }

    fun defaultAutoArk(): AutoArk {
        val (config, savedata) = loadConfigAndSavedata()
        return AutoArk(config, savedata, BlueStacks(config.emulator).launch().device)
    }

    @OptIn(ExperimentalTime::class)
    private fun loadConfigAndSavedata(): Pair<AutoArkConfig, AutoArkSavedata> {
        val config = measureTimedValue { AutoArkConfig.loadConfig(Path(CONFIG_PATH)) }
            .let {
                Logger.debug("载入配置文件；花费 ${it.duration}")
                it.value
            }
        val savedata = measureTimedValue { AutoArkSavedata.loadSavedata(Path(SAVEDATA_PATH)) }
            .let {
                Logger.debug("载入存储文件；花费 ${it.duration}")
                it.value
            }
        return Pair(config, savedata)
    }

    fun defaultDevice(): Device {
        val (config, _) = loadConfigAndSavedata()
        return BlueStacks(config.emulator).launch().device
    }

}

fun main() {
    App.run()
}