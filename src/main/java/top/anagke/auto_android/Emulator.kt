package top.anagke.auto_android

import kotlinx.serialization.Serializable
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.adbProc
import top.anagke.auto_ark.native.killProc
import top.anagke.auto_ark.native.openProc
import top.anagke.auto_ark.native.readText
import java.io.Closeable
import java.io.File
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.reader


const val adbPath = "bin/adb/adb.exe"


@Serializable
sealed class Emulator : Closeable {

    abstract fun open(): Device
    abstract fun isRunning(): Boolean

    companion object {
        fun connect(adbHost: String, adbPort: Int): Device {
            val adbAddress = "${adbHost}:${adbPort}"
            val regex = Regex("""$adbAddress\s*device""")
            while (regex !in adbProc("devices").readText().stdout) {
                adbProc("connect", adbAddress).readText()
            }

            val device = Device(adbAddress)
            while (true) {
                try {
                    device.cap()
                    break
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
            return device
        }
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


    val adbHost: String = "127.0.0.1"

    val adbPort: Int = 21503

    override fun open(): Device {
        val running = isRunning()
        if (running.not()) {
            startMemu()
        }
        return connect(adbHost, adbPort)
    }

    override fun isRunning(): Boolean {
        return "MEmu.exe" in openProc("tasklist", "/fi", "Imagename eq MEmu.exe").readText().stdout
    }

    override fun close() {
        stopMemu()
    }


    private fun startMemu() {
        openProc("${File(location).resolve("MEmu.exe")}", "MEmu")
    }

    private fun stopMemu() {
        killProc("adb.exe").readText()
        killProc("MEmu.exe").readText()
        killProc("MEmuHeadless.exe").readText()
    }

}

@Serializable
class BlueStacks(
    private val blueStacksHome: String = "C:/Program Files/BlueStacks_nxt",
    private val blueStacksData: String = "C:/ProgramData/BlueStacks_nxt",
    private val instance: String = "Nougat32",
    private val adbHost: String = "localhost",
    private val adbPort: Int = 0,
) : Emulator() {

    private fun findAdbPort(): Int {
        if (adbPort > 0) {
            return adbPort
        }

        val confFile = Path(blueStacksData).resolve("bluestacks.conf")
        val props = Properties()
        confFile.reader().use { props.load(it) }

        val portStr = props.getProperty("bst.instance.$instance.status.adb_port") ?: return adbPort

        return portStr
            .trim('"')
            .toInt()
    }

    override fun open(): Device {
        if (isRunning().not()) {
            val executable = File(blueStacksHome).resolve("HD-Player.exe").canonicalPath
            openProc(executable, "--instance", instance)

            Thread.sleep(15000)
        }
        return connect(adbHost, findAdbPort())
    }

    override fun isRunning(): Boolean {
        return "HD-Player.exe" in openProc("tasklist", "/fi", "Imagename eq HD-Player.exe").readText().stdout
    }

    override fun close() {
        killProc("HD-Player.exe")
    }

}