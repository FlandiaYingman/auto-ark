package top.anagke.auto_ark.adb

import kotlinx.serialization.Serializable
import top.anagke.auto_ark.native.killProc
import top.anagke.auto_ark.native.openProc
import top.anagke.auto_ark.native.stderrLog
import top.anagke.auto_ark.native.stdoutLog
import java.io.Closeable


private val log = mu.KotlinLogging.logger { }

const val adbPath = "C:\\Program Files\\Microvirt\\MEmu\\adb.exe"


@Serializable
sealed class Emulator : Closeable {

    abstract val adbHost: String
    abstract val adbPort: Int

    abstract fun open(startupPackage: String, startupActivity: String): Device
    abstract fun isRunning(): Boolean

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
class Memu(
    val location: String,
) : Emulator() {

    override val adbHost: String = "127.0.0.1"

    override val adbPort: Int = 21503


    override fun open(startupPackage: String, startupActivity: String): Device {
        val running = isRunning()
        if (running.not()) {
            startMemu(startupPackage, startupActivity)
        }
        val device = connect()
        if (running) {
            device.stop(startupPackage)
            device.launch(startupPackage, startupActivity)
        }
        return device
    }

    override fun isRunning(): Boolean {
        return "MEmu.exe" in openProc("tasklist", "/fi", "Imagename eq MEmu.exe").stdoutLog()
    }

    override fun close() {
        stopMemu()
    }


    private fun startMemu(startupPackage: String, startupActivity: String) {
        val pa = startupPackage + if (startupActivity.isNotEmpty()) "/$startupActivity" else ""
        openProc(location, "MEmu", "applink", pa)
    }

    private fun stopMemu() {
        killProc("adb.exe").stdoutLog()
        killProc("MEmu.exe").stdoutLog()
        killProc("MEmuHeadless.exe").stdoutLog()
    }

}