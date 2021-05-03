package top.anagke.auto_ark.adb

import mu.KotlinLogging

private val log = KotlinLogging.logger {}

fun main() {
    val xAxisRegex = Regex("""/dev/input/event\d: \w{4} 0035 (\w{8})""")
    val yAxisRegex = Regex("""/dev/input/event\d: \w{4} 0036 (\w{8})""")

    val proc = ProcessBuilder("adb", "shell", "getevent").start()

    var tempX = 0
    var tempY: Int

    proc.inputStream.bufferedReader(Charsets.US_ASCII).forEachLine {
        val xResult = xAxisRegex.matchEntire(it)
        val yResult = yAxisRegex.matchEntire(it)
        if (xResult != null) {
            tempX = xResult.groupValues[1].toInt(16)
        }
        if (yResult != null) {
            tempY = yResult.groupValues[1].toInt(16)
            log.info { "tap($tempX, $tempY)" }
        }
    }
}