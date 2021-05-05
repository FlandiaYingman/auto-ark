package top.anagke.auto_ark

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.Tmpl
import top.anagke.auto_ark.ark.ArkProps
import top.anagke.auto_ark.ark.dailyRoutine
import top.anagke.auto_ark.native.executeElevated
import top.anagke.auto_ark.native.stopProcess
import java.io.File
import kotlin.system.exitProcess

@Serializable
data class AutoProps(
    val arkLauncher: String = "C:/Program Files (x86)/MuMu/emulator/nemu/EmulatorShell/NemuLauncher.exe",
    val arkPackage: String = "com.hypergryph.arknights",
    val adbHost: String = "localhost",
    val adbPort: Int = 7555,
    val arkProps: ArkProps = ArkProps()
)

val autoProps: AutoProps by lazy {
    val file = File("auto-ark-props.yaml")
    if (file.exists()) {
        Yaml.default.decodeFromString(AutoProps.serializer(), file.readText())
    } else {
        file.writeText(Yaml.default.encodeToString(AutoProps.serializer(), AutoProps()))
        exitProcess(-1)
    }
}

fun testTemplate(tmpl: Tmpl) {
    while (true) {
        val diff = tmpl.diff(Device(autoProps.adbHost, autoProps.adbPort).cap())
        println("'${tmpl.name}''s diff   = ${String.format("%.6f", diff)}")
        println("'${tmpl.name}''s result = ${diff < tmpl.threshold}")
        println("=".repeat(32))
    }
}

fun startNemu() {
    //Ensure NEMU is not running
    stopNemu()

    executeElevated(autoProps.arkLauncher, "-p ${autoProps.arkPackage}")
}

fun stopNemu() {
    stopProcess("NemuSVC.exe")
    stopProcess("NemuPlayer.exe")
    stopProcess("NemuHeadless.exe")
}

fun main() {
    startNemu()

    val device = Device(autoProps.adbHost, autoProps.adbPort)
    dailyRoutine(device)

    stopNemu()
}