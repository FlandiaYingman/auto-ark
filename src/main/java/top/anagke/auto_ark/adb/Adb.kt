package top.anagke.auto_ark.adb

import top.anagke.auto_ark.dsl.Timer
import top.anagke.auto_ark.img.Img
import top.anagke.auto_ark.native.logLines

fun adbProc(vararg command: String): Process {
    val procBuilder = ProcessBuilder(listOf(listOf("adb"), command.toList()).flatten())
    return procBuilder.start()
}

fun awaitDevice(adbHost: String, adbPort: Int) {
    adbProc("reconnect")
    val addr = "${adbHost}:${adbPort}"
    do {
        val connectOutput = adbProc("connect", addr).stdoutStr()
        connectOutput.logLines()
    } while (
        connectOutput.contains("cannot") ||
        connectOutput.contains("failed")
    )
}

class Device {

    private val capScheduler = Timer(1000)

    fun cap(): Img = capScheduler.invoke {
        Img(adbProc("exec-out", "screencap -p").stdout())
    }

    fun tap(x: Int, y: Int) {
        adbProc("shell", "input", "tap", "$x", "$y").wait()
    }

    fun back() {
        return adbProc("shell", "input", "keyevent", "4").wait()
    }

    fun input(str: String) {
        return adbProc("shell", "input", "text", str).wait()
    }


    operator fun <T> invoke(opsType: OpsType<T>): T {
        return opsType.invoke(OpsContext(this))
    }

}