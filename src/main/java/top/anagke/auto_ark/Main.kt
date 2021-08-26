package top.anagke.auto_ark

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import top.anagke.auto_ark.ark.LoginConfig
import top.anagke.auto_ark.ark.appConfig
import top.anagke.auto_ark.ark.dailyRoutine

fun main() {
    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    if (appConfig.DEBUG) {
        rootLogger.level = Level.DEBUG
    }

    val emulator = appConfig.emulator
    emulator.use {
        do {
            val timeout = try {
                val device = it.open(startupPackage(), startupActivity())
                dailyRoutine(device)
                false
            } catch (e: Exception) {
                e.printStackTrace()
                true
            }
        } while (timeout)
    }
}

fun startupPackage(): String {
    return when (appConfig.loginConfig.loginType) {
        LoginConfig.LoginType.OFFICIAL -> "com.hypergryph.arknights"
        LoginConfig.LoginType.BILIBILI -> "com.hypergryph.arknights.bilibili"
    }
}

fun startupActivity(): String {
    return when (appConfig.loginConfig.loginType) {
        LoginConfig.LoginType.OFFICIAL -> "com.u8.sdk.U8UnityContext"
        LoginConfig.LoginType.BILIBILI -> ""
    }
}