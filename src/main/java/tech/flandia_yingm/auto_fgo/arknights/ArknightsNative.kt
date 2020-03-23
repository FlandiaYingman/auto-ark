package tech.flandia_yingm.auto_fgo.arknights

import java.io.File
import java.net.InetAddress
import java.net.Socket

object ArknightsNative {

    val nemuLancher = File("D:/Program Files/Arknights/emulator/nemu/EmulatorShell/NemuLauncher.exe").absolutePath
    val nemuArguments = listOf("-p", "com.hypergryph.arknights")

    fun startNemu() {
        val cmd = "\"$nemuLancher\" ${nemuArguments.joinToString(" ")}"
        Socket(InetAddress.getLoopbackAddress(), 990).use { so ->
            so.getOutputStream().bufferedWriter().use {
                it.write(cmd)
                it.flush()
            }
        }
    }

    fun stopNemu() {
    }

}