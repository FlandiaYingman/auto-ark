package dev.flandia.ark

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addPathSource
import dev.flandia.android.device.BlueStacksConf
import dev.flandia.ark.login.ArkLoginConf
import dev.flandia.ark.operate.ArkOperateConf
import dev.flandia.ark.riic.ArkRIICConf
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.copyTo
import kotlin.io.path.notExists

data class AutoArkConfig(
    val 服务器: ArkServer,
    val 模拟器: BlueStacksConf,
    val 登录配置: ArkLoginConf,
    val 行动配置: ArkOperateConf,
    val 基建配置: ArkRIICConf,
) {

    companion object {

        private val baseConfigFile = Path("./config_base.yaml")

        @OptIn(ExperimentalHoplite::class)
        fun loadConfig(configFile: Path): AutoArkConfig {
            if (configFile.notExists()) {
                baseConfigFile.copyTo(configFile)
            }
            return ConfigLoader.builder()
                .addPathSource(configFile)
                .addPathSource(baseConfigFile)
                .withExplicitSealedTypes()
                .build()
                .loadConfigOrThrow()
        }

    }

}