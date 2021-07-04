package top.anagke.auto_ark.native

import mu.KotlinLogging
import org.mozilla.universalchardet.UniversalDetector
import java.io.IOException


private val log = KotlinLogging.logger { }

fun Process.await() {
    waitFor().let { }
}

fun Process.stdout(): ByteArray {
    inputStream.use { _ ->
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

fun Process.stderr(): ByteArray {
    errorStream.use { _ ->
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
        val stderr = stderr()
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
