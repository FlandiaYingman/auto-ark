package top.anagke.auto_ark

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.adbProc
import top.anagke.auto_ark.adb.logLines
import top.anagke.auto_ark.adb.proc
import top.anagke.auto_ark.adb.stdoutStr
import top.anagke.auto_ark.ark.ArkProps
import top.anagke.auto_ark.ark.dailyRoutine
import top.anagke.auto_ark.native.executeAsAdministrator
import java.io.File
import kotlin.system.exitProcess

@Serializable
data class AutoArkProps(
    val nemuLauncher: String = "C:/Program Files (x86)/MuMu/emulator/nemu/EmulatorShell/NemuLauncher.exe",
    val arkPackage: String = "com.hypergryph.arknights",
    val adbHost: String = "localhost",
    val adbPort: Int = 7555,
    val arkProps: ArkProps = ArkProps()
)

val autoArkProps: AutoArkProps by lazy {
    val file = File("auto-ark-props.yaml")
    if (file.exists()) {
        Yaml.default.decodeFromString(AutoArkProps.serializer(), file.readText())
    } else {
        file.writeText(Yaml.default.encodeToString(AutoArkProps.serializer(), AutoArkProps()))
        exitProcess(-1)
    }
}

fun startNemu() {
    executeAsAdministrator(autoArkProps.nemuLauncher, "-p ${autoArkProps.arkPackage}")
    adbProc("kill-server").stdoutStr().logLines()
    adbProc("start-server").stdoutStr().logLines()
    do {
        val connectOutput = adbProc("connect", "${autoArkProps.adbHost}:${autoArkProps.adbPort}").stdoutStr()
        connectOutput.logLines()
    } while (connectOutput.contains("cannot connect to") || connectOutput.contains("failed to connect to"))
}

fun stopNemu() {
    proc("wmic", "process", "where", "\"name='NemuSVC.exe'\"", "delete").stdoutStr().logLines()
    proc("wmic", "process", "where", "\"name='NemuPlayer.exe'\"", "delete").stdoutStr().logLines()
    proc("wmic", "process", "where", "\"name='NemuHeadless.exe'\"", "delete").stdoutStr().logLines()
}

fun main() {
    stopNemu()
    startNemu()
    val device = Device()
    dailyRoutine(device)
    stopNemu()
}