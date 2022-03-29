package top.anagke.auto_android.device

import org.tinylog.kotlin.Logger
import top.anagke.auto_android.native.killProc
import top.anagke.auto_android.native.openProc
import top.anagke.auto_android.native.waitText
import kotlin.io.path.Path
import kotlin.io.path.name

class ADB(
    val adbPath: String,
) {

    companion object {
        val global: ADB = ADB("adb")

    }

    fun cmd(vararg adbCmds: String, serial: String?): Process {
        val commands = if (serial == null) {
            listOf(listOf(adbPath), adbCmds.toList()).flatten()
        } else {
            listOf(listOf(adbPath, "-s", serial), adbCmds.toList()).flatten()
        }
        Logger.trace("run: ${commands.joinToString(separator = " ")}")
        return openProc(*commands.toTypedArray())
    }

    fun sh(vararg shCmds: String, serial: String?): Process {
        return cmd("shell", *shCmds, serial = serial)
    }

    fun reset() {
        val processName = Path(adbPath).name
        killProc(processName)
        this.cmd("kill-server", serial = null).waitText()
        this.cmd("start-server", serial = null).waitText()
    }

}