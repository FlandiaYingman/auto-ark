package top.anagke.auto_ark.adb

import top.anagke.auto_ark.img.Img
import top.anagke.auto_ark.native.openProc
import top.anagke.auto_ark.native.readRaw
import top.anagke.auto_ark.native.readText
import top.anagke.auto_ark.util.minutes
import java.nio.file.Path

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


class Device(
    private val serial: String? = null,
) {

    private fun adbProc(vararg adbCommands: String): Process = adbProc(*adbCommands, serial = serial.orEmpty())

    private fun adbShell(vararg shellCommands: String): Process = adbShell(*shellCommands, serial = serial.orEmpty())


    fun cap(): Img {
        return Img.decode(adbProc("exec-out", "screencap -p").readRaw().stdout)
            ?: throw IllegalStateException("empty screencap")
    }


    fun tap(x: Int, y: Int) {
        log.debug { "Tap ($x, $y), serial='$serial'" }
        adbShell("input", "tap", "$x", "$y").readText()
    }

    fun back() {
        log.debug { "Back, serial='$serial'" }
        adbShell("input", "keyevent", "4").readText()
    }

    fun input(str: String) {
        log.debug { "Input '$str', serial='$serial'" }
        str.forEach {
            adbShell("input", "text", "$it").readText()
        }
    }

    fun swipe(sx: Int, sy: Int, ex: Int, ey: Int, duration: Int) {
        log.debug { "Swipe ($sx, $sy, $ex, $ey, $duration), serial='$serial'" }
        adbShell("input", "swipe", "$sx", "$sy", "$ex", "$ey", "$duration").readText()
    }

    fun nswipe(sx: Int, sy: Int, ex: Int, ey: Int, duration: Int, tail: Int) {
        log.debug { "NSwipe ($sx, $sy, $ex, $ey, $duration), serial='$serial'" }
        adbShell("sh", "/mnt/sdcard/ninput", "swipe", "$sx", "$sy", "$ex", "$ey", "$duration").readText()
    }


    fun launch(activity: AndroidActivity) {
        log.debug { "Launch $activity" }
        adbShell("am", "start", "$activity").readText()
    }

    fun stop(activity: AndroidActivity) {
        log.debug { "Stop $activity" }
        adbShell("am", "force-stop", activity.packageName).readText()
    }

    val focusedActivity: AndroidActivity?
        get() {
            val str = adbShell("dumpsys", "activity", "activities").readText().stdout
            return Regex("""mFocusedActivity: ActivityRecord\{.*? .*? (.*?) .*?}""")
                .find(str)
                ?.groupValues
                ?.get(1)
                ?.let { AndroidActivity.parse(it) }
        }


    fun install(apk: Path) {
        val realApk = apk.toRealPath()
        log.debug { "Install ($realApk), serial='$serial'" }
        adbProc("install", "-r", "$realApk").readText(5.minutes)
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