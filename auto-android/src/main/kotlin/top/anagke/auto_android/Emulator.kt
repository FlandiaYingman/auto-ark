package top.anagke.auto_android

import kotlinx.serialization.Serializable
import top.anagke.auto_android.native.killProc
import top.anagke.auto_android.native.openProc
import top.anagke.auto_android.native.readText
import java.io.Closeable
import java.io.File
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.name
import kotlin.io.path.reader

@Serializable
sealed class Emulator : Closeable {

    abstract val adbPath: String

    abstract fun open(): Device
    abstract fun isRunning(): Boolean

    fun connect(adbHost: String, adbPort: Int): Device {
        val adbAddress = "${adbHost}:${adbPort}"
        val regex = Regex("""$adbAddress\s*device""")
        while (true) {
            val (stdout, stderr) = adbProc(adbPath, "devices").readText()
            if (regex in stdout) {
                break
            }
            if (stderr.isNotBlank()) {
                val processName = Path(adbPath).name
                killProc(processName)
                adbProc(adbPath, "kill-server").readText()
                adbProc(adbPath, "start-server").readText()
            }
            adbProc(adbPath, "connect", adbAddress).readText()
        }

        val device = Device(adbAddress, adbPath)
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

@Serializable
data class BlueStacks(
    val blueStacksHome: String,
    val blueStacksData: String,
    val instance: String,
    val adbHost: String,
    val adbPort: Int,
) : Emulator() {

    private fun findAdbPort(): Int {
        if (adbPort > 0) {
            return adbPort
        }

        val confFile = Path(blueStacksData).resolve("bluestacks.conf")
        val props = Properties()
        confFile.reader().use { props.load(it) }

        val propertyName = "bst.instance.$instance.status.adb_port"
        val portStr = props.getProperty(propertyName) ?: throw NullPointerException("$propertyName cannot be found")

        return portStr
            .trim('"')
            .toInt()
    }

    override val adbPath: String
        get() = Path(blueStacksHome).resolve("HD-Adb.exe").absolutePathString()

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