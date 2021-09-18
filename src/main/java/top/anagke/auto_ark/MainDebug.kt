package top.anagke.auto_ark

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.ark.autoRiic
import top.anagke.auto_ark.ark.login
import top.anagke.auto_ark.ark.operate.autoOperate

fun main() {
    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    rootLogger.level = Level.DEBUG

    val device = Device()
    device.autoOperate()
}