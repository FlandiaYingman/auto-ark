package top.anagke.auto_android

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import top.anagke.auto_android.native.killProc
import top.anagke.auto_android.native.openProc
import top.anagke.auto_android.native.readText
import top.anagke.auto_android.util.MutexException
import top.anagke.auto_android.util.OsMutex
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
    abstract fun isFree(): Boolean

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

    @Contextual
    private var mutex: OsMutex? = null

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
        mutex = OsMutex("bluestacks_$instance")
        return connect(adbHost, findAdbPort())
    }

    override fun isRunning(): Boolean {
        return inTaskList("HD-Player.exe", "HD-Player.exe") &&
                inCommandLine("--instance $instance", "HD-Player")
    }

    override fun isFree(): Boolean {
        return try {
            OsMutex("bluestacks_$instance").close()
            true
        } catch (e: MutexException) {
            false
        }
    }

    override fun close() {
        mutex?.close()
        killProc("HD-Player.exe")
    }

}

fun inTaskList(str: String, processName: String): Boolean {
    val output = openProc("TASKLIST", "/FI", "IMAGENAME eq $processName")
        .readText()
        .stdout
    return str in output
}

fun inCommandLine(str: String, processName: String): Boolean {
    val output = openProc("WMIC", "PROCESS", "WHERE", "CAPTION=\"$processName\"", "GET", "COMMANDLINE")
        .readText()
        .stdout
    return str in output
}


fun findEmulator(emulators: List<Emulator>): Device {
    for (emulator in emulators) {
        try {
            return emulator.open()
        } catch (e: MutexException) {
            continue
        }
    }
    throw Exception("no free emulator is found")
}