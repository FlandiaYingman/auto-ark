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
data class AutoArkCache(
    var farmingPlan: MutableMap<String, Int> = mutableMapOf(),
) {

    companion object {

        fun loadCache(cacheFile: Path): AutoArkCache {
            if (cacheFile.notExists()) saveCache(cacheFile, AutoArkCache())
            return Yaml.default.decodeFromString(cacheFile.readText())
        }

        fun saveCache(cacheFile: Path, cache: AutoArkCache) {
            cacheFile.writeText(Yaml.default.encodeToString(cache))
        }

    }

}