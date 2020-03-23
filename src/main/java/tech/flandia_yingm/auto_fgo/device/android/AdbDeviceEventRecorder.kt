package tech.flandia_yingm.auto_fgo.device.android

import mu.KotlinLogging
import tech.flandia_yingm.auto_fgo.img.Point
import java.io.File
import java.io.IOException
import java.nio.charset.Charset


fun main() {
    val device = AdbDevice("127.0.0.1:7555")
    recordEvents()
}


private fun AndroidEvent.isXEvent() = eventType == 3 && eventCode == 5

private fun AndroidEvent.isYEvent() = eventType == 3 && eventCode == 6

@Throws(IOException::class)
fun recordEvents() {
    val log = KotlinLogging.logger { }

    val recordDir = File("./record")
    recordDir.mkdirs()

    log.info { "Start recording events" }
    val process = Runtime.getRuntime().exec(arrayOf("adb", "shell", "getevent -q"))
    process.inputStream.bufferedReader(Charset.defaultCharset()).use { reader ->
        var xEvent: AndroidEvent? = null
        var yEvent: AndroidEvent? = null
        reader.lines().forEach { line ->
            if (line.isNotBlank()) {
                val event = parse(line)
                if (event.isXEvent()) xEvent = event
                if (event.isYEvent()) yEvent = event
                if (xEvent != null && yEvent != null) {
                    val point = Point(xEvent!!.eventValue.toInt(), yEvent!!.eventValue.toInt())
                    log.info { "Recorded point $point" }
                    xEvent = null
                    yEvent = null
                }
            }
        }
        log.info { "Finish recording events" }
    }
}