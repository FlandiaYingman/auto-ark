package dev.flandia.ark.event_scanner

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.encodeToString
import dev.flandia.android.device.nap
import dev.flandia.android.device.sleep
import dev.flandia.android.img.DetResult
import dev.flandia.android.img.Img
import dev.flandia.android.img.Pos
import dev.flandia.android.img.det
import dev.flandia.android.util.Rect
import dev.flandia.ark.App
import dev.flandia.ark.ArkModule
import dev.flandia.ark.AutoArk
import dev.flandia.ark.operate.OperatePoses.终端_副活动
import dev.flandia.ark.operate.OperatePoses.终端_活动
import dev.flandia.ark.operate.Root
import dev.flandia.ark.operate.Stage
import dev.flandia.ark.resetInterface

class ArtEventScanner(
    auto: AutoArk,
) : ArkModule(auto) {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ArtEventScanner(App.defaultAutoArk()).run()
        }
    }

    override val name: String = "活动扫描模块"

    override fun run(): Unit = device.run {
        val targetEventName = "相见欢"
        val targetZoneName = "识七味"
        val targetStagePrefix = "OR-"

        resetInterface()

        val root: Root = linkedMapOf(targetEventName to linkedMapOf(targetZoneName to linkedMapOf()))
        val possibleEvents = det(cap())
            .filter { targetEventName in it.text }
        for (possibleEvent in possibleEvents + listOf(
            DetResult(Rect(终端_活动, 终端_活动), "", 1.0),
            DetResult(Rect(终端_副活动, 终端_副活动), "", 1.0)
        )) {
            tap(possibleEvent.box.center(), desc = "Possible Event").sleep()
            val possibleZones = det(cap())
                .filter { targetZoneName in it.text }
            for (possibleZone in possibleZones) {
                tap(possibleZone.box.center(), desc = "Possible Event").nap()
                // Size of Stage: (146, 44)

                var i = 0
                var lastCap: Img? = null
                do {
                    val thisCap = cap()
                    if (lastCap?.similarity(thisCap)?.let { it > 0.99 } == true) {
                        break
                    }
                    lastCap = thisCap
                    val possibleStages = det(lastCap)
                        .filter { it.text.trim().startsWith(targetStagePrefix) }
                    for (possibleStage in possibleStages) {
                        println("${possibleStage.box.center() + Pos(i * (1280 - 146), 0)}: ${possibleStage.text}")

                        root[targetEventName]!![targetZoneName]!![possibleStage.text] =
                            Stage(possibleStage.box.center(), i)
                    }

                    dragv(-1280 + 146, 0)
                    i++
                } while (true)

                resetInterface()
                tap(possibleEvent.box.center(), desc = "Possible Event").sleep()
            }
            resetInterface()
        }

        println(exportStages(root))
    }

    private fun exportStages(root: Root): String {
        return Yaml.default.encodeToString(root)
    }

}