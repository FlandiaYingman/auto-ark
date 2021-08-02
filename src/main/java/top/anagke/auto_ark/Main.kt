package top.anagke.auto_ark

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.system.exitProcess

fun main() {
    thread(isDaemon = true) {
        TimeUnit.HOURS.sleep(4)
        exitProcess(1)
    }

    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    if (appConfig.DEBUG) {
        rootLogger.level = Level.DEBUG
    }

    appConfig.emulator.use { emu ->
        val device = emu.open(startupPackage(appConfig.arkConfig.loginType))
        dailyRoutine(device, appConfig.arkConfig)
    }
}