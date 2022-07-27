package top.anagke.auto_android.native


import org.tinylog.kotlin.Logger
import top.anagke.auto_android.native.ProcessReader.Type.STDERR
import top.anagke.auto_android.native.ProcessReader.Type.STDOUT
import top.anagke.auto_android.util.seconds
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.MILLISECONDS




private val readerExecutor: ExecutorService = Executors.newCachedThreadPool { runnable ->
    Executors.defaultThreadFactory().newThread(runnable).also { it.isDaemon = true }
}

private class ProcessReader(
    val proc: Process, val type: Type
) {

    enum class Type { STDOUT, STDERR }

    fun attachRaw(): Callable<ByteArray> = Callable {
        getProcInputStream().use { it.readBytes() }
    }

    fun attachText(charset: Charset? = null): Callable<String> = Callable {
        getProcReader(charset).useLines { lines ->
            lines.onEach { logLine(it) }.joinToString(separator = "\n")
        }
    }

    private fun getProcInputStream(): InputStream = when (type) {
        STDOUT -> proc.inputStream
        STDERR -> proc.errorStream
    }


    private fun getProcReader(charset: Charset? = null): BufferedReader = if (charset == null) {
        when (type) {
            STDOUT -> proc.inputReader()
            STDERR -> proc.errorReader()
        }
    } else {
        when (type) {
            STDOUT -> proc.inputReader(charset)
            STDERR -> proc.errorReader(charset)
        }
    }


    private fun logLine(line: String) {
        when (type) {
            STDOUT -> Logger.trace("$proc: STDOUT> $line")
            STDERR -> Logger.debug("$proc: STDERR> $line")
        }
    }

}


fun Process.waitRaw(timeout: Long = 15.seconds, charset: Charset? = null): ProcessOutput<ByteArray, String> {
    val stdoutFuture = readerExecutor.submit(ProcessReader(this, STDOUT).attachRaw())
    val stderrFuture = readerExecutor.submit(ProcessReader(this, STDERR).attachText(charset))

    val exitValue = waitProcess(timeout)
    val stdout = stdoutFuture.get()
    val stderr = stderrFuture.get()
    return ProcessOutput(stdout, stderr, exitValue)
}

fun Process.waitText(timeout: Long = 15.seconds, charset: Charset? = null): ProcessOutput<String, String> {
    val stdoutFuture = readerExecutor.submit(ProcessReader(this, STDOUT).attachText(charset))
    val stderrFuture = readerExecutor.submit(ProcessReader(this, STDERR).attachText(charset))

    val exitValue = waitProcess(timeout)
    val stdout = stdoutFuture.get()
    val stderr = stderrFuture.get()
    return ProcessOutput(stdout, stderr, exitValue)
}

private fun Process.waitProcess(timeout: Long): Int {
    val exited = waitFor(timeout, MILLISECONDS)
    if (exited.not()) {
        destroyForcibly()
        Logger.warn("process $this timed out after $timeout ms")
    }
    val eventuallyExited = waitFor(5.seconds, MILLISECONDS)
    if (eventuallyExited.not()) {
        throw IOException("process $this not exited after destroying")
    }
    return this.exitValue()
}


data class ProcessOutput<O, E>(
    val stdout: O,
    val stderr: E,
    val exitValue: Int,
)

fun openProc(vararg command: String): Process {
    val procBuilder = ProcessBuilder(*command)
    return procBuilder.start()
        .also { Logger.debug("open process $it, (${command.toList()})") }
}

fun killProc(processName: String): Process {
    return openProc("wmic", "process", "where", "\"name='$processName'\"", "delete")
}
