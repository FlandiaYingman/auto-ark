@file:Suppress("unused")

package top.anagke.auto_android.device

import org.tinylog.kotlin.Logger
import top.anagke.auto_android.img.Img
import top.anagke.auto_android.img.Pos
import top.anagke.auto_android.native.ProcessOutput
import top.anagke.auto_android.native.waitRaw
import top.anagke.auto_android.native.waitText
import top.anagke.auto_android.util.BinResources
import top.anagke.auto_android.util.distance
import top.anagke.auto_android.util.minutes
import top.anagke.auto_android.util.seconds
import kotlin.math.roundToInt

class Device(
    private val serial: String? = null,
    private val adb: ADB = ADB.global,
) {

    companion object


    fun cmd(vararg cmds: String) = adb.cmd(*cmds, serial = serial)

    fun sh(vararg cmds: String) = adb.sh(*cmds, serial = serial)

    private fun app_process(classpath: String, classname: String, vararg args: String) =
        sh("app_process", "-Djava.class.path=$classpath", "/system/bin", classname, *args)


    private val abi by lazy {
        this.cmd("exec-out", "getprop", "ro.product.cpu.abi")
            .waitText()
            .stdout
    }

    private fun initCustomTool() {
        push("bin/adb/swipee.jar", "/data/local/tmp/swipee.jar")
        sh("chmod", "0777", "/data/local/tmp/swipee.jar").waitText()
    }

    init {
        BinResources.init()
        initCustomTool()
    }


    /**
     * Captures current screen.
     *
     * If the current screen is unavailable to capture, throws an
     * [NullPointerException].
     */
    fun cap(): Img {
        val raw = cmd("exec-out", "screencap")
            .waitRaw(timeout = 1.seconds)
            .stdout
        return Img.decodeRaw(1280, 720, raw)
    }

    fun tap(pos: Pos, desc: String = "") = tap(pos.x, pos.y, desc = desc)

    fun tap(x: Int, y: Int, desc: String = "") {
        Logger.debug(formatMsg("Tap ($x, $y)", desc))
        sh("input", "tap", "$x", "$y").waitText()
    }

    fun tapd(x: Int, y: Int, desc: String = "") {
        Logger.debug(formatMsg("Double Tap ($x, $y)", desc))
        sh("input", "tap", "$x", "$y", ";", "input", "tap", "$x", "$y").waitText()
    }

    fun tapl(x: Int, y: Int, duration: Int = 1000, desc: String = "") {
        Logger.debug(formatMsg("Long Tap ($x, $y)", desc))
        sh("input", "swipe", "$x", "$y", "$x", "$y", "$duration").waitText()
    }

    fun tapm(vararg pos: Pos, desc: String = "") {
        Logger.debug(formatMsg("Multiple Tap (${pos.toList()})", desc))
        val cmds = pos
            .map { listOf("input", "tap", "${it.x}", "${it.y}", ";") }
            .flatten()
            .toTypedArray()
        sh(*cmds).waitText()
    }

    fun swipe(sx: Int, sy: Int, ex: Int, ey: Int, speed: Double = 0.5, desc: String = "") {
        Logger.debug(formatMsg("Swipe ($sx, $sy, $ex, $ey, $speed)", desc))
        val k = 1.0
        val duration = (distance(Pos(sx, sy), Pos(ex, ey)) / speed * k).roundToInt()
        sh("input", "swipe", "$sx", "$sy", "$ex", "$ey", "$duration").waitText()
    }

    fun drag(sx: Int, sy: Int, ex: Int, ey: Int, speed: Double = 0.5, desc: String = "") {
        Logger.debug(formatMsg("Drag ($sx, $sy, $ex, $ey, $speed)", desc))
        app_process(
            "/data/local/tmp/swipee.jar",
            "top.anagke.Swipee",
            "exact", "$sx", "$sy", "$ex", "$ey", "$speed"
        ).waitText()
    }

    fun swipev(sx: Int, sy: Int, vx: Int, vy: Int, speed: Double = 0.5, desc: String = "") {
        Logger.debug(formatMsg("Swipe Variation ($sx, $sy, $vx, $vy, $speed)", desc))
        val ex = sx + vx
        val ey = sy + vy
        val k = 1.0
        val duration = (distance(Pos(sx, sy), Pos(ex, ey)) / speed * k).roundToInt()
        sh("input", "swipe", "$sx", "$sy", "$ex", "$ey", "$duration").waitText()
    }

    fun swipev(vx: Int, vy: Int, speed: Double = 0.5, desc: String = "") =
        swipev(1280 / 2, 720 / 2, vx, vy, speed, desc)

    fun dragv(sx: Int, sy: Int, vx: Int, vy: Int, speed: Double = 0.15, desc: String = "") {
        Logger.debug(formatMsg("Drag Variation ($sx, $sy, $vx, $vy, $speed)", desc))
        val ex = sx + vx
        val ey = sy + vy
        app_process(
            "/data/local/tmp/swipee.jar",
            "top.anagke.Swipee",
            "exact", "$sx", "$sy", "$ex", "$ey", "$speed"
        ).waitText()
    }

    fun dragv(vx: Int, vy: Int, speed: Double = 0.15, desc: String = "") =
        dragv(1280 / 2, 720 / 2, vx, vy, speed, desc)

    fun back(description: String = "") {
        Logger.debug(formatMsg("Back", description))
        sh("input", "keyevent", "4").waitText()
    }


    fun input(str: String, desc: String = "") {
        Logger.debug(formatMsg("Input '$str'", desc))
        sh("input", "text", str).waitText()
    }

    fun inputSecret(str: String, desc: String = "") {
        Logger.debug(formatMsg("Input '${str.replace(Regex("."), "*")}'", desc))
        sh("input", "text", str).waitText()
    }


    fun launch(activity: AndroidActivity, desc: String = "") {
        Logger.debug(formatMsg("Launch $activity", desc))
        sh("monkey", "-p", activity.packageName, "1").waitText()
    }

    fun stop(activity: AndroidActivity, desc: String = "") {
        Logger.debug(formatMsg("Stop activity $activity", desc))
        sh("am", "force-stop", activity.packageName).waitText()
    }


    fun dumpsys(activity: AndroidActivity, desc: String = ""): ProcessOutput<String, String> {
        Logger.debug(formatMsg("Dumpsys", desc))
        return sh("dumpsys", "package", activity.packageName).waitText()
    }

    fun install(apk: String, description: String = "") {
        Logger.debug(formatMsg("Install APK $apk", description))
        cmd("install", "-r", apk).waitText(15.minutes)
    }


    fun push(local: String, remote: String, desc: String = "") {
        Logger.debug(formatMsg("Push $remote to $local", desc))
        cmd("push", local, remote).waitText()
    }

    fun pull(remote: String, local: String, desc: String = "") {
        Logger.debug(formatMsg("Pull $remote to $local", desc))
        cmd("pull", remote, local).waitText()
    }

    private fun formatMsg(message: String, desc: String) = "$desc; $this: $message"


    override fun toString(): String {
        return "Device($serial)"
    }

}