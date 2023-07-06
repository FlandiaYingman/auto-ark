package top.anagke.auto_ark

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.nio.file.Path
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText

@Serializable
data class AutoArkSavedata(
    var isFirstRun: Boolean = true,
    var farmingPlans: List<MutableMap<String, Int>> = listOf(),
    var farmingAdaptivePlans: List<String> = listOf(),
) {

    companion object {

        fun loadSavedata(saveFile: Path): AutoArkSavedata {
            if (saveFile.notExists()) saveSaveData(saveFile, AutoArkSavedata())
            return Yaml.default.decodeFromString(saveFile.readText())
        }

        fun saveSaveData(saveFile: Path, savedata: AutoArkSavedata) {
            saveFile.writeText(Yaml.default.encodeToString(savedata))
        }

    }

}