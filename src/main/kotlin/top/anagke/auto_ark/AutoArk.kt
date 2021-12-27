package top.anagke.auto_ark

import com.charleskorn.kaml.Yaml
import com.sksamuel.hoplite.ConfigLoader
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import mu.KotlinLogging
import top.anagke.auto_android.AutoModule
import top.anagke.auto_android.Device
import top.anagke.auto_android.Emulator
import top.anagke.auto_ark.login.ArkLogin
import top.anagke.auto_ark.mission.ArkMission
import top.anagke.auto_ark.operate.ArkOperate
import top.anagke.auto_ark.operate.OperateConfig
import top.anagke.auto_ark.recruit.ArkRecruit
import top.anagke.auto_ark.riic.ArkRiic
import top.anagke.auto_ark.riic.RiicConfig
import top.anagke.auto_ark.store.ArkStore
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText


val logger = KotlinLogging.logger {}

class AutoArk(
    private val config: AutoArkConfig = AutoArkConfig.loadConfig(),
    private val cache: AutoArkCache = AutoArkCache.loadCache(config.cacheLocation),
    private val device: Device = config.emulator.open(),
) {

    companion object {


        const val maxTryTimes: Int = 3

    }

    fun routine() {
        val modules = listOf(
            ArkUpdate(device, config),
            ArkLogin(device, config),
            ArkOperate(device, config.operateConfig, cache),
            ArkRiic(device),
            ArkStore(device),
            ArkMission(device),
            ArkRecruit(device),
        )
        modules.forEach(this::runModule)
    }

    private fun runModule(module: AutoModule) {
        for (tried in 1..maxTryTimes) {
            try {
                module.auto()
                break
            } catch (e: Exception) {
                onModuleError(module, tried, e)
            } finally {
                onModuleEnds()
            }
        }
    }

    private fun onModuleError(module: AutoModule, tried: Int, e: Exception) = device.apply {
        logger.warn(e) { "在运行 ${module.name} 时错误发生，尝试退回到主界面，已尝试 $tried/$maxTryTimes" }
        jumpOut()
    }

    private fun onModuleEnds() = device.apply {
        AutoArkCache.saveCache(config.cacheLocation, cache)
    }

}

data class AutoArkConfig(
    val server: ArkServer,
    val cacheLocation: Path,
    val forceLogin: Boolean,
    val emulator: Emulator,
    val riicConfig: RiicConfig,
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

@Serializable
data class AutoArkCache(
    var farmingPlan: Map<String, Int> = mapOf(),
) {

    companion object {

        fun loadCache(cacheFile: Path): AutoArkCache {
            if (cacheFile.notExists()) saveCache(cacheFile, AutoArkCache())
            return Yaml.default.decodeFromString(cacheFile.readText())
        }

        fun saveCache(cacheFile: Path, cache: AutoArkCache) {
            cacheFile.writeText(Yaml.default.encodeToString(cache))
        }

    }

}