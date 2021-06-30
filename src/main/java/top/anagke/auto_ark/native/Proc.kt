package top.anagke.auto_ark.native

import mu.KotlinLogging
import org.mozilla.universalchardet.UniversalDetector
import java.io.IOException


private val log = KotlinLogging.logger { }

fun Process.await() {
    waitFor().let { }
}

fun Process.stdout(): ByteArray {
    return inputStream.use { it.readBytes() }
}

fun Process.stdoutStr(): String {
    return try {
        val ud = UniversalDetector()
        val stdout = stdout()
        ud.handleData(stdout)
        ud.dataEnd()
        String(stdout, charset(ud.detectedCharset ?: "GBK"))
    } catch (e: IOException) {
        return ""
    }
}

fun Process.stderrStr(): String {
    return try {
        val ud = UniversalDetector()
        val stderr = errorStream.use { it.readBytes() }
        ud.handleData(stderr)
        ud.dataEnd()
        String(stderr, charset(ud.detectedCharset ?: "GBK"))
    } catch (e: IOException) {
        return ""
    }
}

fun Process.stdoutLog(): String {
    return stdoutStr().also { str ->
        str.lines().forEach { if (it.isNotBlank()) log.info { "> $it" } }
    }
}

fun Process.stderrLog(): String {
    return stderrStr().also { str ->
        str.lines().forEach { if (it.isNotBlank()) log.info { "ERR> $it" } }
    }
}


fun openProc(vararg command: String): Process {
    val procBuilder = ProcessBuilder(*command)
    return procBuilder.start()
}

fun killProc(processName: String): Process {
    return openProc("wmic", "process", "where", "\"name='$processName'\"", "delete")
}
