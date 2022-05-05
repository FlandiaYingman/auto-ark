package top.anagke.auto_ark

import com.sksamuel.hoplite.ConfigLoader
import top.anagke.auto_android.device.BlueStacksConf
import top.anagke.auto_ark.operate.OperateConfig
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.copyTo
import kotlin.io.path.notExists

data class AutoArkConfig(
    val server: ArkServer,
    val emulator: BlueStacksConf,
    val operateConfig: OperateConfig,
) {

    companion object {

        private val baseConfigFile = Path("./config_base.yaml")

        fun loadConfig(configFile: Path): AutoArkConfig {
            if (configFile.notExists()) {
                baseConfigFile.copyTo(configFile)
            }
            return ConfigLoader().loadConfigOrThrow(listOfNotNull(configFile.toString(), baseConfigFile.toString()))
        }

    }

}