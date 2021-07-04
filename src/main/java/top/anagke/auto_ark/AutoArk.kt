package top.anagke.auto_ark

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.Emulator
import top.anagke.auto_ark.adb.Memu
import top.anagke.auto_ark.ark.ArkLoginContext
import top.anagke.auto_ark.ark.ArkOperateConfig
import top.anagke.auto_ark.ark.RecruitConfig
import top.anagke.auto_ark.ark.RiicConfig
import top.anagke.auto_ark.ark.autoMission
import top.anagke.auto_ark.ark.autoOperate
import top.anagke.auto_ark.ark.autoRiic
import top.anagke.auto_ark.ark.login
import java.io.File
import kotlin.system.exitProcess

@Serializable
data class AppConfig(
    val DEBUG: Boolean = false,
    val emulator: Emulator = Memu("C:/Program Files/Microvirt/MEmu/Memu.exe"),
    val arkConfig: ArkConfig = ArkConfig()
)

@Serializable
data class ArkConfig(
    val loginType: ArkLoginContext = ArkLoginContext.OfficialLogin("<username>", "<password>"),
    val riicConfig: RiicConfig = RiicConfig(),
    val recruitConfig: RecruitConfig = RecruitConfig(),
    val arkOperateConfig: ArkOperateConfig = ArkOperateConfig(),
)


private val log = KotlinLogging.logger {}
val appConfig: AppConfig = run {
    val file = File("config.yaml")
    if (file.exists().not()) {
        val configYaml = Yaml.default.encodeToString(AppConfig.serializer(), AppConfig())
        file.writeText(configYaml)
        log.error { "无法找到配置文件，创建配置文件模板" }
        exitProcess(-1)
    }
    val configYaml = file.readText()
    Yaml.default.decodeFromString(AppConfig.serializer(), configYaml)
}


fun main() {
    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    if (appConfig.DEBUG) {
        rootLogger.level = Level.DEBUG
    }

    val device = appConfig.emulator.open(startupPackage(appConfig.arkConfig.loginType))

    dailyRoutine(device, appConfig.arkConfig)
}

fun startupPackage(arkLoginContext: ArkLoginContext): String {
    return when (arkLoginContext) {
        is ArkLoginContext.OfficialLogin -> "com.hypergryph.arknights/com.u8.sdk.U8UnityContext"
        is ArkLoginContext.BilibiliLogin -> "com.hypergryph.arknights.bilibili"
    }
}

fun dailyRoutine(device: Device, config: ArkConfig) {
    device.login(config.loginType)
    device.autoRiic(config.riicConfig)
//    device.autoRecruit(config.recruitConfig)
    device.autoOperate(config.arkOperateConfig)
    device.autoMission()
//TODO:    device.autoCreditStore()
}