package top.anagke.auto_ark

import com.sksamuel.hoplite.ConfigLoader
import top.anagke.auto_android.device.BlueStacks
import top.anagke.auto_ark.operate.OperateConfig
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import kotlin.io.path.exists

data class AutoArkConfig(
    val server: ArkServer,
    val cacheLocation: Path,
    val forceLogin: Boolean,
    val emulator: BlueStacks,
    val operateConfig: OperateConfig,
) {

    companion object {

        private val baseConfigFiles = listOf(
            Path.of("./base-config.toml"),
            //...
        )

        private val defaultConfigFiles = listOf(
            Path.of("./config.toml"),
            //...
        )


        fun loadConfig(givenConfigFiles: List<Path> = emptyList()): AutoArkConfig {
            val baseConfigFile = findExisting(baseConfigFiles) ?: throw NoSuchFileException("$baseConfigFiles")
            val configFile = findExisting((givenConfigFiles + defaultConfigFiles))
            return ConfigLoader().loadConfigOrThrow(listOfNotNull(configFile, baseConfigFile))
        }

        private fun findExisting(paths: List<Path>) = paths.find { it.exists() }

    }

}