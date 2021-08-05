package top.anagke.auto_ark

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import top.anagke.auto_ark.ark.LoginConfig
import top.anagke.auto_ark.ark.appConfig
import top.anagke.auto_ark.ark.dailyRoutine
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.system.exitProcess

fun main() {
    thread(isDaemon = true) {
        timeoutListener()
    }

    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    if (appConfig.DEBUG) {
        rootLogger.level = Level.DEBUG
    }

    appConfig.emulator.use { emu ->
        val device = emu.open(startupPackage())
        dailyRoutine(device)
    }
}

private fun timeoutListener() {
    TimeUnit.HOURS.sleep(4)
    exitProcess(1)
}

fun startupPackage(): String {
    return when (appConfig.loginConfig.loginType) {
        LoginConfig.LoginType.OFFICIAL -> "com.hypergryph.arknights/com.u8.sdk.U8UnityContext"
        LoginConfig.LoginType.BILIBILI -> "com.hypergryph.arknights.bilibili"
    }
}