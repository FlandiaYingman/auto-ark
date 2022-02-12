package top.anagke.auto_ark

import top.anagke.auto_android.device.BlueStacks
import kotlin.io.path.Path

object App {

    const val CONFIG_PATH = "./config.yaml"
    const val SAVEDATA_PATH = "./savedata.yaml"


    fun run() {
        val config = AutoArkConfig.loadConfig(Path(CONFIG_PATH))
        val savedata = AutoArkSavedata.loadSavedata(Path(SAVEDATA_PATH))
        BlueStacks(config.emulator).launch().use {
            AutoArk(config, savedata, it.device).doRoutine()
        }
    }

    fun defaultAutoArk(): AutoArk {
        val config = AutoArkConfig.loadConfig(Path(CONFIG_PATH))
        val savedata = AutoArkSavedata.loadSavedata(Path(SAVEDATA_PATH))
        return AutoArk(config, savedata, BlueStacks(config.emulator).launch().device)
    }

}

fun main() {
    App.run()
}