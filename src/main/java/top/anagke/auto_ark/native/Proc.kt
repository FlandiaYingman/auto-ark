package top.anagke.auto_ark.native

import mu.KotlinLogging
import org.mozilla.universalchardet.UniversalDetector
import java.io.IOException

private val log = KotlinLogging.logger { }

fun Process.await() {
    waitFor().let { }
}

fun Process.stdOut(): ByteArray {
    inputStream.use {
        val breakTime = System.currentTimeMillis() + 3000
        do {
            val available = inputStream.available()
            if (available == -1) {
                return byteArrayOf()
            }
        } while (available == 0 && System.currentTimeMillis() <= breakTime)
        if (inputStream.available() == 0) {
            this.destroyForcibly()
            return byteArrayOf()
        }
        return inputStream.readBytes()
    }
}

fun Process.stdErr(): ByteArray {
    errorStream.use {
        val breakTime = System.currentTimeMillis() + 3000
        do {
            val available = errorStream.available()
            if (available == -1) {
                return byteArrayOf()
            }
        } while (available == 0 && System.currentTimeMillis() <= breakTime)
        if (errorStream.available() == 0) {
            this.destroyForcibly()
            return byteArrayOf()
        }
        return errorStream.readBytes()
    }
}

fun Process.stdOutStr(): String {
    return try {
        val ud = UniversalDetector()
        val stdout = stdOut()
        ud.handleData(stdout)
        ud.dataEnd()
        String(stdout, charset(ud.detectedCharset ?: "GBK"))
    } catch (e: IOException) {
        return ""
    }
}

fun Process.stdErrStr(): String {
    return try {
        val ud = UniversalDetector()
        val stderr = stdErr()
        ud.handleData(stderr)
        ud.dataEnd()
        String(stderr, charset(ud.detectedCharset ?: "GBK"))
    } catch (e: IOException) {
        return ""
    }
}

fun Process.stdOutStrLog(): String {
    return stdOutStr().also { str ->
        str.lines().forEach { if (it.isNotBlank()) log.info { "> $it" } }
    }
}

fun Process.stdErrStrLog(): String {
    return stdErrStr().also { str ->
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
