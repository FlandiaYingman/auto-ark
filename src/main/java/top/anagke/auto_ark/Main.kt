package top.anagke.auto_ark

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit.MINUTES
import kotlin.concurrent.thread
import kotlin.system.exitProcess

fun main() {
    thread {
        threadTimeout()
    }

    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    if (appConfig.DEBUG) {
        rootLogger.level = Level.DEBUG
    }

    val device = appConfig.emulator.open(startupPackage(appConfig.arkConfig.loginType))

    dailyRoutine(device, appConfig.arkConfig)
}

fun threadTimeout() {
    MINUTES.sleep(30)
    exitProcess(100)
}