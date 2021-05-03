package top.anagke.auto_ark.native

import com.sun.jna.Native
import com.sun.jna.platform.win32.ShellAPI
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.win32.StdCallLibrary
import mu.KotlinLogging
import top.anagke.auto_ark.adb.stdoutStr

interface Shell32 : ShellAPI, StdCallLibrary {
    fun ShellExecuteA(
        hwnd: HWND?,
        lpOperation: String?,
        lpFile: String?,
        lpParameters: String?,
        lpDirectory: String?,
        nShowCmd: Int
    ): WinDef.HINSTANCE?

    companion object {
        val INSTANCE = Native.loadLibrary("shell32", Shell32::class.java) as Shell32
    }
}

fun executeElevated(command: String?, args: String?) {
    Shell32.INSTANCE.ShellExecuteA(null, "runas", command, args, null, 1)
}

fun stopProcess(processName: String) {
    proc("wmic", "process", "where", "\"name='$processName'\"", "delete").stdoutStr().logLines()
}

fun proc(vararg command: String): Process {
    val procBuilder = ProcessBuilder(*command)
    return procBuilder.start()
}

private val log = KotlinLogging.logger { }
fun String.logLines() = lines().forEach { if (it.isNotBlank()) log.info { "> $it" } }