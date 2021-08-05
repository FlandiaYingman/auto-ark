package top.anagke.auto_ark.ark

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import top.anagke.auto_ark.serializer.LocalDateTimeSerializer
import top.anagke.kio.file.notExists
import top.anagke.kio.file.text
import java.io.File
import java.time.LocalDateTime

@Serializable
data class AutoArkSave(
    @Serializable(with = LocalDateTimeSerializer::class)
    var lastAutoRiicTime: LocalDateTime = LocalDateTime.MIN,
    @Serializable(with = LocalDateTimeSerializer::class)
    var lastAutoRecruitTime: LocalDateTime = LocalDateTime.MIN,
    @Serializable(with = LocalDateTimeSerializer::class)
    var lastAnnihilationTime: LocalDateTime = LocalDateTime.MIN,
)


private val appSaveFile = File("save.yaml")

val appSave = loadAppSave()

private fun loadAppSave(): AutoArkSave {
    if (appSaveFile.notExists()) {
        appSaveFile.text = Yaml.default.encodeToString(AutoArkSave.serializer(), AutoArkSave())
    }
    return Yaml.default.decodeFromString(AutoArkSave.serializer(), appSaveFile.text)
}

private fun AutoArkSave.save() {
    appSaveFile.text = Yaml.default.encodeToString(AutoArkSave.serializer(), this)
}


fun AutoArkSave.edit(block: AutoArkSave.() -> Unit) {
    try {
        block()
    } finally {
        save()
    }
}