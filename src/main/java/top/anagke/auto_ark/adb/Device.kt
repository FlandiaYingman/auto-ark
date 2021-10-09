package top.anagke.auto_ark.adb

import top.anagke.auto_ark.img.Img
import top.anagke.auto_ark.native.await
import top.anagke.auto_ark.native.openProc
import top.anagke.auto_ark.native.stdOut
import top.anagke.auto_ark.native.stdOutStr

private val log = mu.KotlinLogging.logger { }


fun adbProc(vararg adbCommands: String, serial: String = ""): Process {
    val commands = if (serial.isEmpty()) {
        listOf(listOf(adbPath), adbCommands.toList()).flatten()
    } else {
        listOf(listOf(adbPath, "-s", serial), adbCommands.toList()).flatten()
    }
    return openProc(*commands.toTypedArray())
}

fun adbShell(vararg shellCommands: String, serial: String = ""): Process {
    return adbProc("shell", *shellCommands, serial = serial)
}


class Device(val serial: String? = null) {

    private fun adbProc(vararg adbCommands: String): Process = adbProc(*adbCommands, serial = serial.orEmpty())

    private fun adbShell(vararg shellCommands: String): Process = adbShell(*shellCommands, serial = serial.orEmpty())

    fun cap(): Img {
        return Img.decode(adbProc("exec-out", "screencap -p").stdOut())
            ?: throw IllegalStateException("empty screencap")
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
        adbShell("input", "swipe", "$sx", "$sy", "$ex", "$ey", "$duration").await()
    }

    fun nswipe(sx: Int, sy: Int, ex: Int, ey: Int, duration: Int, tail: Int) {
        log.debug { "NSwipe ($sx, $sy, $ex, $ey, $duration), serial='$serial'" }
        adbShell("sh", "/mnt/sdcard/ninput", "swipe", "$sx", "$sy", "$ex", "$ey", "$duration").await()
    }


    fun launch(activity: AndroidActivity) {
        log.debug { "Launch $activity" }
        adbProc("shell", "am", "start", "$activity").await()
    }

    fun stop(activity: AndroidActivity) {
        log.debug { "Stop $activity" }
        adbProc("shell", "am", "force-stop", activity.packageName).await()
    }

    val focusedActivity: AndroidActivity?
        get() {
            val str = adbProc("shell", "dumpsys", "activity", "activities").stdOutStr()
            return Regex("""mFocusedActivity: ActivityRecord\{.*? .*? (.*?) .*?}""")
                .find(str)
                ?.groupValues
                ?.get(1)
                ?.let { AndroidActivity.parse(it) }
        }

}

data class AndroidActivity(
    val packageName: String,
    val activityName: String,
) {
    companion object {
        fun parse(string: String): AndroidActivity {
            val split = string.split("/")
            require(split.size == 2)
            val packageName = split[0]
            val activityName = split[1]
            return AndroidActivity(packageName, activityName)
        }
    }

    override fun toString(): String {
        if (activityName.isEmpty()) {
            return packageName
        }
        return "$packageName/$activityName"
    }
}