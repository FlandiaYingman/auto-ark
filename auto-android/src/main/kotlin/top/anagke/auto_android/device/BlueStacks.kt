package top.anagke.auto_android.device

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import top.anagke.auto_android.native.openProc
import top.anagke.auto_android.native.waitText
import top.anagke.auto_android.util.MutexException
import top.anagke.auto_android.util.OsMutex
import top.anagke.auto_android.util.Win32
import java.io.File
import java.util.*

@Serializable
data class BlueStacks(
    val blueStacksHome: String,
    val blueStacksData: String,
) : Emulator {

    @Contextual
    val bsConf: Map<String, String>
        get() {
            val props = Properties()
            File(blueStacksData, "bluestacks.conf").reader().use { props.load(it) }
            return props.toList().associate { (key, value) ->
                val k = key.toString()
                val v = value.toString().removeSurrounding("\"")
                (k to v)
            }
        }

    @Contextual
    val bsInstances: List<BlueStacksInstance>
        get() = bsConf.keys.mapNotNull {
            val regex = Regex("""bst\.instance\.(.*?)\..*""")
            val result = regex.matchEntire(it) ?: return@mapNotNull null
            result.groups[1]!!.value
        }.map { BlueStacksInstance(it) }.distinct().toList()

    inner class BlueStacksInstance(val instance: String) {

        fun isRunning(): Boolean {
            val bsExec = "HD-Player.exe"
            return Win32.procExists(bsExec) && Win32.procCmdExists(instance, bsExec)
        }

        fun run(): OsMutex {
            return OsMutex("bs_$instance.lock").also {
                val executable = File(blueStacksHome).resolve("HD-Player.exe")
                openProc(executable.canonicalPath, "--instance", instance)
            }
        }

        fun port(): Int {
            val propertyName = "bst.instance.$instance.status.adb_port"
            return bsConf[propertyName]!!.toInt()
        }

    }


    override fun launch(): EmulatorHandle {
        val adb = ADB(File(blueStacksHome, "HD-Adb.exe").canonicalPath)
        adb.cmd("start-server", serial = null).waitText()

        val (instance, mutex) = grabInstance() ?: throw Exception("no available instance was found")

        while (true) {
            val adbHost = "127.0.0.1"
            val adbPort = instance.port()
            val device = connect(adb, adbHost, adbPort)
            if (device != null) {
                return EmulatorHandle(device, mutex)
            }
        }
    }

    private fun grabInstance(): Pair<BlueStacksInstance, OsMutex>? {
        for (instance in bsInstances) {
            try {
                val mutex = instance.run()
                return instance to mutex
            } catch (e: MutexException) {
                continue
            }
        }
        return null
    }


    companion object {

        private fun connect(adb: ADB, adbHost: String, adbPort: Int): Device? {
            val addr = "${adbHost}:${adbPort}"
            while (true) {
                val (stdout, stderr) = adb.cmd("devices", serial = null).waitText()
                when {
                    // the stderr prints some message: reset ADB
                    stderr.isNotBlank() -> {
                        adb.reset()
                    }
                    // the address exist, the state is device: nothing to do
                    stdout.contains(Regex("$addr\\s*device")) -> {
                        break
                    }
                    // the address exist, but the state is offline: reconnect device
                    stdout.contains(Regex("$addr\\s*offline")) -> {
                        adb.cmd("reconnect", serial = addr).waitText()
                    }
                    // the address doesn't even exist - not connected
                    else -> {
                        adb.cmd("connect", addr, serial = null).waitText()
                    }
                }
            }

            val device = Device(addr, adb)
            while (true) {
                try {
                    device.cap()
                    break
                } catch (_: NullPointerException) {
                }
            }
            return device
        }

    }

}

