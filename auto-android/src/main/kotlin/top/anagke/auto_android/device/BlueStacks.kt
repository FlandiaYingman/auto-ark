package top.anagke.auto_android.device

import org.tinylog.kotlin.Logger
import top.anagke.auto_android.native.Platform
import top.anagke.auto_android.native.openProc
import top.anagke.auto_android.native.waitText
import top.anagke.auto_android.util.MutexException
import top.anagke.auto_android.util.OsMutex
import top.anagke.auto_android.util.Win32
import top.anagke.auto_android.util.seconds
import java.io.File
import java.util.*

class BlueStacks(config: BlueStacksConf) : Emulator {

    val blueStacksHome: String =
        File(config.blueStacksHome ?: Platform.getPlatform().getBlueStacksInstallDir()).canonicalPath
    val blueStacksData: String =
        File(config.blueStacksData ?: Platform.getPlatform().getBlueStacksDataDir()).canonicalPath


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
        Logger.info("启动蓝叠模拟器中")

        val adb = ADB(File(blueStacksHome, "HD-Adb.exe").canonicalPath)
        adb.cmd("start-server", serial = null).waitText()
        Logger.info("启动蓝叠模拟器的 ADB 模块")

        val (instance, mutex) = grabInstance() ?: throw Exception("no available instance was found")
        Logger.info("启动蓝叠模拟器的 ${instance.instance} 实例")

        while (true) {
            val adbHost = "127.0.0.1"
            val adbPort = instance.port()
            val device = connect(adb, adbHost, adbPort)
            if (device != null) {
                Logger.info("启动蓝叠模拟器的 ${instance.instance} 实例，启动完成")
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
            val (stdout, stderr) = adb.cmd("devices", serial = null).waitText(timeout = 1.seconds)
            when {
                // the stderr prints some message: reset ADB
                stderr.isNotBlank() -> {
                    adb.reset()
                    return null
                }
                // the address exist, the state is device: nothing to do
                stdout.contains(Regex("$addr\\s*device")) -> {
                }
                // the address exist, but the state is offline: reconnect device
                stdout.contains(Regex("$addr\\s*offline")) -> {
                    adb.cmd("reconnect", serial = addr).waitText(timeout = 5.seconds)
                    return null
                }
                // the address doesn't even exist - not connected
                else -> {
                    adb.cmd("connect", addr, serial = null).waitText(timeout = 5.seconds)
                    return null
                }
            }

            val device = Device(addr, adb)
            return try {
                device.cap()
                device
            } catch (_: NullPointerException) {
                null
            }
        }

    }

}
