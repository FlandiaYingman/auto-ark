package top.anagke.auto_ark.ark

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import top.anagke.auto_ark.adb.Emulator
import top.anagke.auto_ark.adb.Memu
import top.anagke.kio.file.notExists
import top.anagke.kio.file.text
import java.io.File

@Serializable
data class AutoArkConfig(
    val DEBUG: Boolean = false,
    val emulator: Emulator = Memu("C:/Program Files/Microvirt/MEmu/Memu.exe"),
    val loginConfig: LoginConfig = LoginConfig(),
    val riicConfig: RiicConfig = RiicConfig(),
    val recruitConfig: RecruitConfig = RecruitConfig(),
    val operateConfig: OperateConfig = OperateConfig(),
)

private val appConfigFile = File("config.yaml")

val appConfig = loadAppConfig()


fun loadAppConfig(): AutoArkConfig {
    if (appConfigFile.notExists()) {
        appConfigFile.text = Yaml.default.encodeToString(AutoArkConfig.serializer(), AutoArkConfig())
    }
    return Yaml.default.decodeFromString(AutoArkConfig.serializer(), appConfigFile.text)
}
