package dev.flandia.ark.operate

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import kotlinx.serialization.Serializable
import dev.flandia.android.device.nap
import dev.flandia.android.img.Pos
import dev.flandia.android.img.det

typealias Root = MutableMap<String, Event>
typealias Event = MutableMap<String, Zone>
typealias Zone = MutableMap<String, Stage>

@Serializable
data class Stage(
    val position: Pos,
    val page: Int,
) {
    companion object {
        private val root: Root = Stage::class.java
            .getResourceAsStream("root.yaml")!!
            .use(Yaml.default::decodeFromStream)
        val stagesAsOperation = root.flatMap { (eventName, event) ->
            event.flatMap { (zoneName, zone) ->
                zone.mapValues { (stageName, stage) ->
                    ActOperation(stageName, "${eventName}，${zoneName}", "") {
                        val possibleZone = det(cap()).firstOrNull { zoneName in it.text } ?: return@ActOperation
                        tap(possibleZone.box.center(), desc = zoneName).nap()
                        repeat(stage.page) { dragv(-1280 + 146, 0, desc = "移动 Page") }
                        tap(stage.position, desc = stageName)
                    }
                }.toList()
            }
        }.toMap()
    }
}