package top.anagke.auto_ark

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.addPathSource
import top.anagke.auto_android.device.BlueStacksConf
import top.anagke.auto_ark.operate.OperateConfig
import top.anagke.auto_ark.riic.RIICConfig
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.copyTo
import kotlin.io.path.notExists

data class AutoArkConfig(
    val server: ArkServer,
    val emulator: BlueStacksConf,
    val operateConfig: OperateConfig,
    val 基建: RIICConfig
) {

    companion object {

        private val baseConfigFile = Path("./config_base.yaml")

        fun loadConfig(configFile: Path): AutoArkConfig {
            if (configFile.notExists()) {
                baseConfigFile.copyTo(configFile)
            }
            return ConfigLoader.builder()
                .addPathSource(configFile)
                .addPathSource(baseConfigFile)
                .build()
                .loadConfigOrThrow()
        }

    }

}