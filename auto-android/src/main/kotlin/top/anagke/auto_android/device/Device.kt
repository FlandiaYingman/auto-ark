@file:Suppress("unused")

package top.anagke.auto_android.device

import org.tinylog.kotlin.Logger
import top.anagke.auto_android.img.Img
import top.anagke.auto_android.native.ProcessOutput
import top.anagke.auto_android.native.waitRaw
import top.anagke.auto_android.native.waitText
import top.anagke.auto_android.util.BinResources
import top.anagke.auto_android.util.Pos
import top.anagke.auto_android.util.distance
import top.anagke.auto_android.util.minutes
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
            .waitRaw()
            .stdout
        return Img.decodeRaw(1280, 720, raw)
    }

    fun tap(pos: Pos, description: String = "") = tap(pos.x, pos.y, description = description)

    fun tap(x: Int, y: Int, description: String = "") {
        Logger.debug(genLogMessage("Tap ($x, $y)", description))
        sh("input", "tap", "$x", "$y").waitText()
    }

    fun tapd(x: Int, y: Int, description: String = "") {
        Logger.debug(genLogMessage("Double Tap ($x, $y)", description))
        sh("input", "tap", "$x", "$y", ";", "input", "tap", "$x", "$y").waitText()
    }

    fun tapl(x: Int, y: Int, duration: Int = 1000, description: String = "") {
        Logger.debug(genLogMessage("Long Tap ($x, $y)", description))
        sh("input", "swipe", "$x", "$y", "$x", "$y", "$duration").waitText()
    }

    fun tapm(vararg pos: Pos, description: String = "") {
        Logger.debug(genLogMessage("Multiple Tap (${pos.toList()})", description))
        val cmds = pos
            .map { listOf("input", "tap", "${it.x}", "${it.y}", ";") }
            .flatten()
            .toTypedArray()
        sh(*cmds).waitText()
    }

    fun swipe(sx: Int, sy: Int, ex: Int, ey: Int, speed: Double = 0.5, description: String = "") {
        Logger.debug(genLogMessage("Swipe ($sx, $sy, $ex, $ey, $speed)", description))
        val k = 1.0
        val duration = (distance(Pos(sx, sy), Pos(ex, ey)) / speed * k).roundToInt()
        sh("input", "swipe", "$sx", "$sy", "$ex", "$ey", "$duration").waitText()
    }

    fun drag(sx: Int, sy: Int, ex: Int, ey: Int, speed: Double = 0.5, description: String = "") {
        Logger.debug(genLogMessage("Drag ($sx, $sy, $ex, $ey, $speed)", description))
        app_process(
            "/data/local/tmp/swipee.jar",
            "top.anagke.Swipee",
            "exact", "$sx", "$sy", "$ex", "$ey", "$speed"
        ).waitText()
    }

    fun swipev(sx: Int, sy: Int, vx: Int, vy: Int, speed: Double = 0.5, description: String = "") {
        Logger.debug(genLogMessage("Swipe Variation ($sx, $sy, $vx, $vy, $speed)", description))
        val ex = sx + vx
        val ey = sy + vy
        val k = 1.0
        val duration = (distance(Pos(sx, sy), Pos(ex, ey)) / speed * k).roundToInt()
        sh("input", "swipe", "$sx", "$sy", "$ex", "$ey", "$duration").waitText()
    }

    fun dragv(sx: Int, sy: Int, vx: Int, vy: Int, speed: Double = 0.15, description: String = "") {
        Logger.debug(genLogMessage("Drag Variation ($sx, $sy, $vx, $vy, $speed)", description))
        val ex = sx + vx
        val ey = sy + vy
        app_process(
            "/data/local/tmp/swipee.jar",
            "top.anagke.Swipee",
            "exact", "$sx", "$sy", "$ex", "$ey", "$speed"
        ).waitText()
    }

    fun back(description: String = "") {
        Logger.debug(genLogMessage("Back", description))
        sh("input", "keyevent", "4").waitText()
    }


    fun input(str: String, description: String = "") {
        Logger.debug(genLogMessage("Input '$str'", description))
        sh("input", "text", str).waitText()
    }

    fun inputSecret(str: String, description: String = "") {
        Logger.debug(genLogMessage("Input '${str.replace(Regex("."), "*")}'", description))
        sh("input", "text", str).waitText()
    }


    fun launch(activity: AndroidActivity, description: String = "") {
        Logger.debug(genLogMessage("Launch $activity", description))
        sh("monkey", "-p", activity.packageName, "1").waitText()
    }

    fun stop(activity: AndroidActivity, description: String = "") {
        Logger.debug(genLogMessage("Stop activity $activity", description))
        sh("am", "force-stop", activity.packageName).waitText()
    }


    fun dumpsys(activity: AndroidActivity, description: String = ""): ProcessOutput<String, String> {
        Logger.debug(genLogMessage("Dumpsys", description))
        return sh("dumpsys", "package", activity.packageName).waitText()
    }

    fun install(apk: String, description: String = "") {
        Logger.debug(genLogMessage("Install APK $apk", description))
        cmd("install", "-r", apk).waitText(5.minutes)
    }


    fun push(local: String, remote: String, description: String = "") {
        Logger.debug(genLogMessage("Push $remote to $local", description))
        cmd("push", local, remote).waitText()
    }

    fun pull(remote: String, local: String, description: String = "") {
        Logger.debug(genLogMessage("Pull $remote to $local", description))
        cmd("pull", remote, local).waitText()
    }

    private fun genLogMessage(message: String, description: String) = "$description; $this: $message"


    override fun toString(): String {
        return "Device($serial)"
    }

}