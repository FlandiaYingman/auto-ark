package top.anagke.auto_ark.adb

import top.anagke.auto_ark.img.Img
import top.anagke.auto_ark.native.await
import top.anagke.auto_ark.native.openProc
import top.anagke.auto_ark.native.stdOut
import top.anagke.auto_ark.native.stdOutStr

private val log = mu.KotlinLogging.logger { }

class Device(val serial: String? = null) {

    fun cap(): Img {
        return Img(adbProc("exec-out", "screencap -p").stdOut())
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


    private fun adbProc(vararg adbCommands: String): Process {
        val commands = if (serial == null) {
            listOf(listOf(adbPath), adbCommands.toList()).flatten()
        } else {
            listOf(listOf(adbPath, "-s", serial), adbCommands.toList()).flatten()
        }
        return openProc(*commands.toTypedArray())
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