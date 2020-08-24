
package tech.flandia_yingm.auto_fgo.device.android

import java.nio.charset.Charset
import java.util.concurrent.TimeUnit.SECONDS
import java.util.concurrent.TimeoutException

class Adb(val serial: String) {

    init {
        do {
            val successful = connect(serial)
        } while (!successful)
    }

    fun shellCmd(cmds: List<String>): String {
        cmdAdbS(cmds)
    }

}

fun connect(serial: String): Boolean {
    val output = cmdAdb(listOf("connect", serial))
    return when {
        output.contains("connected to") -> true
        output.contains("unable to connect to") -> false
        else -> throw RuntimeException("Invalid output of adb connect: \n$output\n")
    }
}

private fun cmdAdb(cmds: List<String>): String {
    val proc = ProcessBuilder(listOf("adb") + cmds).redirectErrorStream(true).start()
    if (proc.waitFor(10, SECONDS)) {
        return proc.inputStream.bufferedReader(Charset.defaultCharset()).use { it.readText() }
    } else {
        val output = proc.inputStream.bufferedReader(Charset.defaultCharset()).use { it.readText() }
        throw TimeoutException("Timeout before the process exits, output=\n$output\n")
    }
}

private fun cmdAdbS(serial: String, cmds: List<String>): String {
    val proc = ProcessBuilder(listOf("adb", "-s", serial) + cmds).redirectErrorStream(true).start()
    if (proc.waitFor(10, SECONDS)) {
        return proc.inputStream.bufferedReader(Charset.defaultCharset()).use { it.readText() }
    } else {
        val output = proc.inputStream.bufferedReader(Charset.defaultCharset()).use { it.readText() }
        throw TimeoutException("Timeout before the process exits, output=\n$output\n")
    }
}

private fun
