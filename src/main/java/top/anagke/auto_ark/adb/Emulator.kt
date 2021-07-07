package top.anagke.auto_ark.adb

import kotlinx.serialization.Serializable
import top.anagke.auto_ark.appConfig
import top.anagke.auto_ark.dsl.Timer
import top.anagke.auto_ark.img.Img
import top.anagke.auto_ark.native.await
import top.anagke.auto_ark.native.executeElevated
import top.anagke.auto_ark.native.killProc
import top.anagke.auto_ark.native.openProc
import top.anagke.auto_ark.native.stderrLog
import top.anagke.auto_ark.native.stdout
import top.anagke.auto_ark.native.stdoutLog
import java.io.Closeable
import kotlin.concurrent.thread


private val log = mu.KotlinLogging.logger { }

private const val adbPath = "adb.exe"


class Device(val serial: String? = null) {

    private val capScheduler = Timer(1000)

    fun cap(): Img {
        return capScheduler.invoke {
            Img(adbProc("exec-out", "screencap -p").stdout())
        }
    }


    fun tap(x: Int, y: Int) {
        log.debug { "Tap ($x, $y), serial='$serial'" }
        adbProc("shell", "input", "tap", "$x", "$y").await()
    }

    fun back() {
        log.debug { "Back, serial='$serial'" }
        return adbProc("shell", "input", "keyevent", "4").await()
    }

    fun input(str: String) {
        log.debug { "Input '$str', serial='$serial'" }
        return adbProc("shell", "input", "text", str).await()
    }

    fun swipe(sx: Int, sy: Int, ex: Int, ey: Int, duration: Int) {
        log.debug { "Swipe ($sx, $sy, $ex, $ey, $duration), serial='$serial'" }
        adbProc("shell", "input", "swipe", "$sx", "$sy", "$ex", "$ey", "$duration").await()
    }


    private fun adbProc(vararg adbCommands: String): Process {
        val commands = if (serial == null) {
            listOf(listOf(adbPath), adbCommands.toList()).flatten()
        } else {
            listOf(listOf(adbPath, "-s", serial), adbCommands.toList()).flatten()
        }
        return openProc(*commands.toTypedArray())
    }

}


@Serializable
sealed class Emulator : Closeable {

    abstract val adbHost: String
    abstract val adbPort: Int

    abstract fun open(startupPackage: String): Device

    fun connect(): Device {
        val addr = "${adbHost}:${adbPort}"
        do {
            val proc = openProc(adbPath, "connect", addr)
            val stdout = proc.stdoutLog()
            val stderr = proc.stderrLog()
        } while (
            stdout.contains("no") ||
            stdout.contains("cannot") ||
            stdout.contains("failed") ||
            stderr.isNotBlank()
        )

        val dev = Device(addr)
        do {
            val data = dev.cap().data
        } while (data.isEmpty())

        return dev
    }

}

@Serializable
class Nemu(
    val location: String,
) : Emulator() {

    override val adbHost: String get() = "localhost"
    override val adbPort: Int get() = 7555

    override fun open(startupPackage: String): Device {
        if (appConfig.DEBUG.not()) Runtime.getRuntime().addShutdownHook(Thread { stopNemu() })
        startNemu(startupPackage)
        return connect()
    }

    override fun close() {
        stopNemu()
    }

    private fun startNemu(startupPackage: String) {
        stopNemu() //Ensure Nemu is not running
        executeElevated(location, "-p $startupPackage")
    }

    private fun stopNemu() {
        listOf(
            killProc("NemuSVC.exe"),
            killProc("NemuPlayer.exe"),
            killProc("NemuHeadless.exe"),
        ).forEach(Process::stdoutLog)
    }

}

@Serializable
class Memu(
    val location: String,
) : Emulator() {

    override val adbHost: String = "127.0.0.1"

    override val adbPort: Int = 21503


    override fun open(startupPackage: String): Device {
        if (appConfig.DEBUG.not()) {
            Runtime.getRuntime().addShutdownHook(Thread { stopMemu() })
        }
        startMemu(startupPackage)
        return connect()
    }

    override fun close() {
        stopMemu()
    }


    private fun startMemu(startupPackage: String) {
        stopMemu() //Ensure BlueStacks is not running
        openProc(location, "MEmu", "applink", startupPackage)
    }

    private fun stopMemu() {
        killProc("adb.exe").stdoutLog()
        killProc("MEmu.exe").stdoutLog()
        killProc("MEmuHeadless.exe").stdoutLog()
    }

}