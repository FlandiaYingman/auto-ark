package top.anagke.auto_ark.adb

import top.anagke.auto_ark.dsl.Timer
import top.anagke.auto_ark.img.Img
import top.anagke.auto_ark.native.await
import top.anagke.auto_ark.native.openProc
import top.anagke.auto_ark.native.stdout
import top.anagke.auto_ark.native.stdoutLog

private val log = mu.KotlinLogging.logger { }

class Device(val serial: String? = null) {

    private val capScheduler = Timer(1000)

    fun cap(): Img {
        return capScheduler.invoke {
            Img(adbProc("exec-out", "screencap -p").stdout())
        }
    }


    fun tap(x: Int, y: Int) {
        log.debug { "Tap ($x, $y), serial='$serial'" }
        adbProc("shell", "input", "tap", "$x", "$y").await()
    }

    fun back() {
        log.debug { "Back, serial='$serial'" }
        return adbProc("shell", "input", "keyevent", "4").await()
    }

    fun input(str: String) {
        log.debug { "Input '$str', serial='$serial'" }
        str.forEach {
            adbProc("shell", "input", "text", "$it").await()
        }
    }

    fun swipe(sx: Int, sy: Int, ex: Int, ey: Int, duration: Int) {
        log.debug { "Swipe ($sx, $sy, $ex, $ey, $duration), serial='$serial'" }
        adbProc("shell", "input", "swipe", "$sx", "$sy", "$ex", "$ey", "$duration").await()
    }


    fun launch(packageName: String, activityName: String) {
        log.debug { "Launch $packageName" }
        val pa = packageName + if (activityName.isNotEmpty()) "/$activityName" else ""
        adbProc("shell", "am", "start", pa).await()
    }

    fun stop(packageName: String) {
        log.debug { "Stop $packageName" }
        adbProc("shell", "am", "force-stop", packageName).await()
    }


    private fun adbProc(vararg adbCommands: String): Process {
        val commands = if (serial == null) {
            listOf(listOf(adbPath), adbCommands.toList()).flatten()
        } else {
            listOf(listOf(adbPath, "-s", serial), adbCommands.toList()).flatten()
        }
        return openProc(*commands.toTypedArray())
    }

}