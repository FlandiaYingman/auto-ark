package top.anagke.auto_android.native

import mu.KotlinLogging
import top.anagke.auto_android.native.ProcessReader.Type.STDERR
import top.anagke.auto_android.native.ProcessReader.Type.STDOUT
import top.anagke.auto_android.util.seconds
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.MILLISECONDS

private val log = KotlinLogging.logger { }


private val readerExecutor: ExecutorService = Executors.newCachedThreadPool { runnable ->
    Executors.defaultThreadFactory().newThread(runnable).also { it.isDaemon = true }
}

private class ProcessReader(
    val proc: Process, val type: Type
) {

    enum class Type { STDOUT, STDERR }

    fun attachRaw(): Callable<ByteArray> = Callable {
        procInputStream.use { it.readBytes() }
    }

    fun attachText(): Callable<String> = Callable {
        procReader.useLines { lines ->
            lines.onEach { logLine(it) }.joinToString(separator = "\n")
        }
    }

    private val procInputStream: InputStream
        get() = when (type) {
            STDOUT -> proc.inputStream
            STDERR -> proc.errorStream
        }


    private val procReader: BufferedReader
        get() = when (type) {
            STDOUT -> proc.inputReader()
            STDERR -> proc.errorReader()
        }


    private fun logLine(line: String) {
        when (type) {
            STDOUT -> log.trace { "STDOUT> $line" }
            STDERR -> log.debug { "STDERR> $line" }
        }
    }

}


fun Process.readRaw(timeout: Long = 15.seconds): ProcessOutput<ByteArray, String> {
    val stdoutFuture = readerExecutor.submit(ProcessReader(this, STDOUT).attachRaw())
    val stderrFuture = readerExecutor.submit(ProcessReader(this, STDERR).attachText())

    waitProcess(timeout)
    val stdout = stdoutFuture.get()
    val stderr = stderrFuture.get()
    return ProcessOutput(stdout, stderr)
}

fun Process.readText(timeout: Long = 15.seconds): ProcessOutput<String, String> {
    val stdoutFuture = readerExecutor.submit(ProcessReader(this, STDOUT).attachText())
    val stderrFuture = readerExecutor.submit(ProcessReader(this, STDERR).attachText())

    waitProcess(timeout)
    val stdout = stdoutFuture.get()
    val stderr = stderrFuture.get()
    return ProcessOutput(stdout, stderr)
}

private fun Process.waitProcess(timeout: Long) {
    val exited = waitFor(timeout, MILLISECONDS)
    if (exited.not()) {
        destroyForcibly()
        log.warn { "process $this timed out after $timeout ms" }
    }
    val eventuallyExited = waitFor(5.seconds, MILLISECONDS)
    if (eventuallyExited.not()) {
        throw IOException("process $this not exited after destroying")
    }
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
