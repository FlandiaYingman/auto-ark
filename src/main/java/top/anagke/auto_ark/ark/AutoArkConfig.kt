package top.anagke.auto_ark.ark

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import top.anagke.auto_ark.adb.BlueStacks
import top.anagke.auto_ark.adb.Emulator
import top.anagke.auto_ark.ark.operate.OperateConfig
import top.anagke.auto_ark.ark.recruit.RecruitConfig
import top.anagke.auto_ark.ark.riic.RiicConfig
import top.anagke.kio.file.notExists
import top.anagke.kio.file.text
import java.io.File

@Serializable
data class AutoArkConfig(
    val emulator: Emulator = BlueStacks(),
    val riicConfig: RiicConfig = RiicConfig(),
    val recruitConfig: RecruitConfig = RecruitConfig(),
    val operateConfig: OperateConfig = OperateConfig(),
)

private val appConfigFile = File("config.yaml")

val appConfig = loadAppConfig()


fun loadAppConfig(): AutoArkConfig {
    if (appConfigFile.notExists()) {
        saveAppConfig(AutoArkConfig())
    }
    return Yaml.default.decodeFromString(AutoArkConfig.serializer(), appConfigFile.text)
}

fun saveAppConfig(autoArkConfig: AutoArkConfig) {
    appConfigFile.text = Yaml.default.encodeToString(AutoArkConfig.serializer(), autoArkConfig)
}
