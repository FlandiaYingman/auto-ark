package top.anagke.auto_ark.event_scanner

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.encodeToString
import top.anagke.auto_android.device.nap
import top.anagke.auto_android.device.sleep
import top.anagke.auto_android.img.Img
import top.anagke.auto_android.img.Pos
import top.anagke.auto_android.img.det
import top.anagke.auto_ark.App
import top.anagke.auto_ark.ArkModule
import top.anagke.auto_ark.AutoArk
import top.anagke.auto_ark.operate.Root
import top.anagke.auto_ark.operate.Stage
import top.anagke.auto_ark.resetInterface

/** 收取未收取的邮件。 */
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
        val targetEventName = "火山旅梦"
        val targetZoneName = "温泉假日"
        val targetStagePrefix = "SL-"

        resetInterface()

        val root: Root = linkedMapOf(targetEventName to linkedMapOf(targetZoneName to linkedMapOf()))
        val possibleEvents = det(cap())
            .filter { targetEventName in it.text }
        for (possibleEvent in possibleEvents) {
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