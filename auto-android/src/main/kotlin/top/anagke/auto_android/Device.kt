package top.anagke.auto_android

import top.anagke.auto_android.img.Img
import top.anagke.auto_android.native.ProcessOutput
import top.anagke.auto_android.native.openProc
import top.anagke.auto_android.native.readRaw
import top.anagke.auto_android.native.readText
import top.anagke.auto_android.util.minutes
import java.nio.file.Path

private val log = mu.KotlinLogging.logger { }


fun adbProc(adbPath: String, vararg adbCommands: String, serial: String = ""): Process {
    val commands = if (serial.isEmpty()) {
        listOf(listOf(adbPath), adbCommands.toList()).flatten()
    } else {
        listOf(listOf(adbPath, "-s", serial), adbCommands.toList()).flatten()
    }
    return openProc(*commands.toTypedArray())
}

fun adbShell(adbPath: String, vararg shellCommands: String, serial: String = ""): Process {
    return adbProc(adbPath, "shell", *shellCommands, serial = serial)
}


class Device(
    private val serial: String? = null,
    private val adbPath: String = "adb.exe",
) {

    init {
        initCustomTool()
    }

    private fun initCustomTool() {
        push("bin/adb/swiper.jar", "/sdcard/swiper.jar")
    }


    private fun adbProc(vararg adbCommands: String): Process =
        adbProc(adbPath, *adbCommands, serial = serial.orEmpty())

    private fun adbShell(vararg shellCommands: String): Process =
        adbShell(adbPath, *shellCommands, serial = serial.orEmpty())

    private fun adbAppProcess(classpath: String, classname: String, vararg args: String): Process =
        adbShell("app_process", "-Djava.class.path=$classpath", "/system/bin", classname, *args)


    fun cap(): Img {
        return Img.decode(adbProc("exec-out", "screencap -p").readRaw().stdout)
            ?: throw IllegalStateException("empty screencap")
    }


    fun tap(x: Int, y: Int) {
        log.debug { "Tap ($x, $y), serial='$serial'" }
        adbShell("input", "tap", "$x", "$y").readText()
    }

    fun tapd(x: Int, y: Int) {
        log.debug { "Double Tap ($x, $y), serial='$serial'" }
        adbShell("input", "tap", "$x", "$y", ";", "input", "tap", "$x", "$y").readText()
    }

    fun input(str: String) {
        log.debug { "Input '$str', serial='$serial'" }
        adbShell("input", "text", str).readText()
    }

    fun back() {
        log.debug { "Back, serial='$serial'" }
        adbShell("input", "keyevent", "4").readText()
    }

    fun swipe(sx: Int, sy: Int, ex: Int, ey: Int, duration: Int = 500) {
        log.debug { "Swipe ($sx, $sy, $ex, $ey, $duration), serial='$serial'" }
        adbShell("input", "swipe", "$sx", "$sy", "$ex", "$ey", "$duration").readText()
    }

    fun drag(sx: Int, sy: Int, ex: Int, ey: Int, speed: Double = 0.5) {
        log.debug { "Drag ($sx, $sy, $ex, $ey, $speed), serial='$serial'" }
        adbAppProcess(
            "/sdcard/swiper.jar",
            "top.anagke.Input",
            "upless-exact", "$sx", "$sy", "$ex", "$ey", "$speed"
        ).readText()
    }

    fun dragd(x: Int, y: Int, dx: Int, dy: Int, speed: Double = 0.5) {
        log.debug { "DragD ($x, $y, $dx, $dy, $speed), serial='$serial'" }
        adbAppProcess(
            "/sdcard/swiper.jar",
            "top.anagke.Input",
            "upless-exact", "$x", "$y", "${x + dx}", "${y + dy}", "$speed"
        ).readText()
    }

    fun tap(sx: Int, sy: Int, ex: Int, ey: Int, x: Int, y: Int) {
        drag(sx, sy, ex, ey).nap()
        tap(x, y)
    }


    fun launch(activity: AndroidActivity) {
        log.debug { "Launch $activity" }
        adbShell("monkey", "-p", activity.packageName, "1").readText()
    }

    fun stop(activity: AndroidActivity) {
        log.debug { "Stop $activity" }
        adbShell("am", "force-stop", activity.packageName).readText()
    }


    fun dumpsys(activity: AndroidActivity): ProcessOutput<String, String> {
        return adbShell("dumpsys", "package", activity.packageName).readText()
    }

    fun install(apk: Path) {
        val realApk = apk.toRealPath()
        log.debug { "Install ($realApk), serial='$serial'" }
        adbProc("install", "-r", "$realApk").readText(5.minutes)
    }


    fun push(local: String, remote: String) {
        log.debug { "Push $local to $remote" }
        adbProc("push", local, remote).readText()
    }

}

data class AndroidActivity(
    val packageName: String,
    val activityName: String,
) {
    override fun toString(): String {
        if (activityName.isEmpty()) {
            return packageName
        }
        return "$packageName/$activityName"
    }
}