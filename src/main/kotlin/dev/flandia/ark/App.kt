package dev.flandia.ark

import org.tinylog.kotlin.Logger
import dev.flandia.android.device.BlueStacks
import dev.flandia.android.device.Device
import kotlin.io.path.Path
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

object App {

    const val CONFIG_PATH = "./config.yaml"
    const val SAVEDATA_PATH = "./savedata.yaml"


    fun run() {
        Logger.info("启动自动方舟")
        val (config, savedata) = loadConfigAndSavedata()
        Logger.info("已读取配置文件、储存文件")
        BlueStacks(config.模拟器).launch().use {
            Logger.info("已启动模拟器")
            AutoArk(config, savedata, it.device).doRoutine()
        }
    }

    fun defaultAutoArk(): AutoArk {
        val (config, savedata) = loadConfigAndSavedata()
        return AutoArk(config, savedata, BlueStacks(config.模拟器).launch().device)
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
        return BlueStacks(config.模拟器).launch().device
    }

}

fun main() {
    App.run()
}