package top.anagke.auto_android.native

import mu.KotlinLogging
import org.mozilla.universalchardet.UniversalDetector
import top.anagke.auto_android.native.ProcessReader.Type.STDERR
import top.anagke.auto_android.native.ProcessReader.Type.STDOUT
import top.anagke.auto_android.util.seconds
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.text.Charsets.UTF_8

private val log = KotlinLogging.logger { }


private val readerExecutor: ExecutorService = Executors.newCachedThreadPool { runnable ->
    Executors.defaultThreadFactory().newThread(runnable).also { it.isDaemon = true }
}

private class ProcessReader(val stream: InputStream, val type: Type) {

    enum class Type { STDOUT, STDERR }

    fun attachRaw(): Callable<ByteArray> = Callable {
        stream.buffered()
            .use {
                it.readBytes()
            }
    }

    fun attachText(): Callable<String> = Callable {
        stream.bufferedReader(charset = UTF_8)
            .useLines { sequence ->
                sequence
                    .map { it.toByteArray(charset = UTF_8) }
                    .map { it.detectCharsetToString() }
                    .onEach { it.logLine() }
                    .joinToString(separator = "\n")
            }
    }

    private fun String.logLine() {
        when (type) {
            STDOUT -> log.trace { "STDOUT> $this" }
            STDERR -> log.debug { "STDERR> $this" }
        }
    }

}


fun Process.readRaw(timeout: Long = 15.seconds): ProcessOutput<ByteArray, String> {
    val stdoutFuture = readerExecutor.submit(ProcessReader(inputStream, STDOUT).attachRaw())
    val stderrFuture = readerExecutor.submit(ProcessReader(errorStream, STDERR).attachText())

    val exited = waitFor(timeout, MILLISECONDS)
    if (exited.not()) {
        destroyForcibly()
        log.warn { "process $this timed out after $timeout ms" }
    }
    val eventuallyExited = waitFor(5.seconds, MILLISECONDS)
    if (eventuallyExited.not()) {
        throw IOException("process $this not exited after destroying")
    }
    val stdout = stdoutFuture.get()
    val stderr = stderrFuture.get()
    return ProcessOutput(stdout, stderr)
}

fun Process.readText(timeout: Long = 15.seconds): ProcessOutput<String, String> {
    val stdoutFuture = readerExecutor.submit(ProcessReader(inputStream, STDOUT).attachText())
    val stderrFuture = readerExecutor.submit(ProcessReader(errorStream, STDERR).attachText())

    val exited = waitFor(timeout, MILLISECONDS)
    if (exited.not()) {
        destroyForcibly()
        log.warn { "process $this timed out after $timeout ms" }
    }
    val eventuallyExited = waitFor(5.seconds, MILLISECONDS)
    if (eventuallyExited.not()) {
        throw IOException("process $this not exited after destroying")
    }
    val stdout = stdoutFuture.get()
    val stderr = stderrFuture.get()
    return ProcessOutput(stdout, stderr)
}

private fun ByteArray.detectCharsetToString(): String {
    val detector = UniversalDetector()
    detector.handleData(this)
    detector.dataEnd()
    return String(this, detector.detectedCharset?.let { charset(it) } ?: Charset.defaultCharset())
}


data class ProcessOutput<O, E>(
    val stdout: O,
    val stderr: E,
)

fun openProc(vararg command: String): Process {
    val procBuilder = ProcessBuilder(*command)
    return procBuilder.start()
}

fun killProc(processName: String): Process {
    return openProc("wmic", "process", "where", "\"name='$processName'\"", "delete")
}
