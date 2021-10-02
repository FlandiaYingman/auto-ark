package top.anagke.auto_ark.adb

import kotlinx.serialization.Serializable
import top.anagke.auto_ark.native.killProc
import top.anagke.auto_ark.native.openProc
import top.anagke.auto_ark.native.stdErrStrLog
import top.anagke.auto_ark.native.stdOutStrLog
import java.io.Closeable
import java.io.File


const val adbPath = "bin/adb/adb.exe"


@Serializable
sealed class Emulator : Closeable {

    abstract val adbHost: String
    abstract val adbPort: Int

    abstract fun open(): Device
    abstract fun isRunning(): Boolean

    fun connect(): Device {
        val addr = "${adbHost}:${adbPort}"
        do {
            val proc = openProc(adbPath, "connect", addr)
            val stdout = proc.stdOutStrLog()
            val stderr = proc.stdErrStrLog()
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
    private val location: String,
) : Emulator() {

    init {
        File("bin/adb/")
            .listFiles()
            ?.forEach { it.copyTo(File(location).resolveSibling(it.name), overwrite = true) }
    }


    override val adbHost: String = "127.0.0.1"

    override val adbPort: Int = 21503


    override fun open(): Device {
        val running = isRunning()
        if (running.not()) {
            startMemu()
        }
        return connect()
    }

    override fun isRunning(): Boolean {
        return "MEmu.exe" in openProc("tasklist", "/fi", "Imagename eq MEmu.exe").stdErrStrLog()
    }

    override fun close() {
        stopMemu()
    }


    private fun startMemu() {
        openProc("${File(location).resolve("MEmu.exe")}", "MEmu")
    }

    private fun stopMemu() {
        killProc("adb.exe").stdOutStrLog()
        killProc("MEmu.exe").stdOutStrLog()
        killProc("MEmuHeadless.exe").stdOutStrLog()
    }

}