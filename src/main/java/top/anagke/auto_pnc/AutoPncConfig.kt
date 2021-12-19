package top.anagke.auto_pnc

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import top.anagke.auto_android.BlueStacks
import top.anagke.auto_android.Emulator
import top.anagke.kio.file.notExists
import top.anagke.kio.file.text
import java.io.File

@Serializable
data class AutoPncConfig(
    val emulator: Emulator = BlueStacks(),
    val username: String = "<username>",
    val password: String = "<password>",
)

private val appConfigFile = File("config_pnc.yaml")

val pncConfig = loadAppConfig()


fun loadAppConfig(): AutoPncConfig {
    if (appConfigFile.notExists()) {
        saveAppConfig(AutoPncConfig())
    }
    return Yaml.default.decodeFromString(AutoPncConfig.serializer(), appConfigFile.text)
}

fun saveAppConfig(autoPncConfig: AutoPncConfig) {
    appConfigFile.text = Yaml.default.encodeToString(AutoPncConfig.serializer(), autoPncConfig)
}
