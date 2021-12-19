package top.anagke.auto_ark.adb

import mu.KotlinLogging
import top.anagke.auto_android.adbPath

private val log = KotlinLogging.logger {}

fun main() {
    val xAxisRegex = Regex("""/dev/input/event\d: \w{4} 0035 (\w{8})""")
    val yAxisRegex = Regex("""/dev/input/event\d: \w{4} 0036 (\w{8})""")
    val tapRegex = Regex("""/dev/input/event\d: \w{4} 0039 0{8}""")

    val proc = ProcessBuilder(adbPath, "shell", "getevent").start()

    var tempX = 0L
    var tempY = 0L

    proc.inputStream.bufferedReader(Charsets.US_ASCII).forEachLine {
        val xResult = xAxisRegex.matchEntire(it)
        val yResult = yAxisRegex.matchEntire(it)
        if (xResult != null) tempX = xResult.groupValues[1].toLong(16)
        if (yResult != null) tempY = yResult.groupValues[1].toLong(16)
        if (tapRegex.matches(it)) {
            log.info { "tap($tempX, $tempY).sleep()" }
        }
    }
}