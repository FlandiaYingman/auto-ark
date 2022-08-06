package top.anagke.auto_android.util

import top.anagke.auto_android.native.openProc
import top.anagke.auto_android.native.waitText

object Win32 {

    fun procExists(processName: String): Boolean {
        val output = openProc("TASKLIST", "/FI", "IMAGENAME eq $processName")
            .waitText()
            .stdout
        return processName in output
    }

    fun procCmdExists(str: String, processName: String): Boolean {
        val output = openProc("WMIC", "PROCESS", "WHERE", "CAPTION=\"$processName\"", "GET", "COMMANDLINE")
            .waitText()
            .stdout
        return str in output
    }

    fun closeBy(where: String): Process {
        return openProc("wmic", "process", where, "delete")
    }

    fun getBy(where: String): Process {
        return openProc("wmic", "process", where, "get")
    }

}