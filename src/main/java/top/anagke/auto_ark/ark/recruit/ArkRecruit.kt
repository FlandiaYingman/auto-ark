package top.anagke.auto_ark.ark.recruit

import kotlinx.serialization.Serializable
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.assert
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.match
import top.anagke.auto_ark.adb.matched
import top.anagke.auto_ark.adb.sleep
import top.anagke.auto_ark.adb.which
import top.anagke.auto_ark.adb.whileNotMatch
import top.anagke.auto_ark.ark.atMainScreen
import top.anagke.auto_ark.ark.jumpOut
import top.anagke.auto_ark.ark.recruit.ArkRecruitCalculator.RecruitOperator
import top.anagke.auto_ark.ark.recruit.RecruitTag.*
import top.anagke.auto_ark.ark.template
import top.anagke.auto_ark.img.Img
import top.anagke.auto_ark.img.Tmpl
import top.anagke.auto_ark.img.crop
import top.anagke.auto_ark.img.invert
import top.anagke.auto_ark.img.ocrTesseract
import java.awt.Rectangle
import java.util.stream.Collectors


@Serializable
data class RecruitConfig(
    val retain6SOperators: Boolean = true,
    val retain5SOperators: Boolean = true,
    val retain4SOperators: Boolean = false,
)

// 公开招募界面
private val atRecruitSlotsScreen = template("recruit/atRecruitSlotsScreen.png")
// 能够刷新TAG
private val canRefreshTag = template("recruit/canRefreshTag.png", diff = 0.01)
private val atRecruitScreen = template("recruit/atRecruitScreen.png")

// 公开招募1号栏位可用
private val isRecruitSlot1Available = template("recruit/isRecruitSlot1Available.png", diff = 0.01)
private val isRecruitSlot1Completed = template("recruit/isRecruitSlot1Completed.png", diff = 0.01)
private val isRecruitSlot1Recruiting = template("recruit/isRecruitSlot1Recruiting.png", diff = 0.01)
// 公开招募2号栏位可用
private val isRecruitSlot2Available = template("recruit/isRecruitSlot2Available.png", diff = 0.01)
private val isRecruitSlot2Completed = template("recruit/isRecruitSlot2Completed.png", diff = 0.01)
private val isRecruitSlot2Recruiting = template("recruit/isRecruitSlot2Recruiting.png", diff = 0.01)
// 公开招募3号栏位可用
private val isRecruitSlot3Available = template("recruit/isRecruitSlot3Available.png", diff = 0.01)
private val isRecruitSlot3Completed = template("recruit/isRecruitSlot3Completed.png", diff = 0.01)
private val isRecruitSlot3Recruiting = template("recruit/isRecruitSlot3Recruiting.png", diff = 0.01)
// 公开招募4号栏位可用
private val isRecruitSlot4Available = template("recruit/isRecruitSlot4Available.png", diff = 0.01)
private val isRecruitSlot4Completed = template("recruit/isRecruitSlot4Completed.png", diff = 0.01)
private val isRecruitSlot4Recruiting = template("recruit/isRecruitSlot4Recruiting.png", diff = 0.01)


private enum class RecruitSlot(
    val isAvailable: Tmpl,
    val isRecruiting: Tmpl,
    val isCompleted: Tmpl,
) {
    SLOT1(
        isRecruitSlot1Available,
        isRecruitSlot1Recruiting,
        isRecruitSlot1Completed
    ),
    SLOT2(
        isRecruitSlot2Available,
        isRecruitSlot2Recruiting,
        isRecruitSlot2Completed
    ),
    SLOT3(
        isRecruitSlot3Available,
        isRecruitSlot3Recruiting,
        isRecruitSlot3Completed
    ),
    SLOT4(
        isRecruitSlot4Available,
        isRecruitSlot4Recruiting,
        isRecruitSlot4Completed
    ),
}


private enum class RecruitTag(val screenRect: Rectangle) {
    TAG1(Rectangle(375, 360, 144, 46)),
    TAG2(Rectangle(542, 360, 144, 46)),
    TAG3(Rectangle(709, 360, 144, 46)),
    TAG4(Rectangle(375, 432, 144, 46)),
    TAG5(Rectangle(542, 432, 144, 46)),
}

private fun parse(cap: Img): Map<RecruitTag, String> {
    return RecruitTag.values().toList()
        .parallelStream()
        .map { it to ocrTesseract(invert(crop(cap, it.screenRect))) }
        .collect(Collectors.toList())
        .toMap()
}

class ArkRecruit(
    private val device: Device,
    private val config: RecruitConfig,
) {

    private var hasRecruitmentPermit = true

    private var hasExpeditedPlan = true

    fun auto() = device.apply {
        assert(atMainScreen)

        tap(1000, 510) //公开招募
        await(atRecruitSlotsScreen)

        RecruitSlot.values().forEach { slot ->
            autoSlot(slot)
        }

        jumpOut()
    }

    private fun autoSlot(slot: RecruitSlot) = device.apply {
        while (true) {
            when (which(slot.isAvailable, slot.isRecruiting, slot.isCompleted)) {
                slot.isAvailable -> {
                    if (!hasRecruitmentPermit) break
                    tapSlot(slot)
                    startRecruit(config)
                    if (!hasExpeditedPlan) break
                }
                slot.isRecruiting -> {
                    if (!hasExpeditedPlan) break
                    tapSlot(slot)
                    expediteRecruit(slot)
                    if (!hasExpeditedPlan) break
                }
                slot.isCompleted -> {
                    tapSlot(slot)
                    completeRecruit(slot)
                    if (!hasRecruitmentPermit) break
                }
            }
        }
    }

    private fun Device.tapSlot(slot: RecruitSlot) {
        when (slot) {
            RecruitSlot.SLOT1 -> tap(475, 380)
            RecruitSlot.SLOT2 -> tap(1101, 380)
            RecruitSlot.SLOT3 -> tap(505, 664)
            RecruitSlot.SLOT4 -> tap(1103, 661)
        }
        sleep()
    }


    private fun Device.startRecruit(config: RecruitConfig) {
        val tags = parse(cap())
        val (tagCombination, possibleOperators) = ArkRecruitCalculator.calculateBest(tags.values.toList())
        val maximumRarity = possibleOperators.maxOf(RecruitOperator::rarity)
        when (maximumRarity) {
            5, 4 -> {
            }
            else -> {
                if (tags[TAG1] in tagCombination) tap(438, 383)
                if (tags[TAG2] in tagCombination) tap(613, 379)
                if (tags[TAG3] in tagCombination) tap(771, 383)
                if (tags[TAG4] in tagCombination) tap(452, 456)
                if (tags[TAG5] in tagCombination) tap(601, 451)
            }
        }
        if (maximumRarity <= 2 && match(canRefreshTag)) {
            tap(972, 408) //刷新TAG
            tap(877, 508) //确认刷新TAG
            sleep()
            startRecruit(config)
            return
        }

        tap(450, 300) //增加时限到”9：00：00“
        tap(977, 588) // 开始招募
        sleep()

        await(atRecruitSlotsScreen, atRecruitScreen)
        if (matched(atRecruitScreen)) {
            hasRecruitmentPermit = false

            back()
            await(atRecruitSlotsScreen)
        }
    }

    private fun Device.expediteRecruit(slot: RecruitSlot) {
        tap(955, 518).sleep()
        await(slot.isCompleted, slot.isRecruiting)
        if (matched(slot.isRecruiting)) {
            hasExpeditedPlan = false
            return
        }
    }

    private fun Device.completeRecruit(slot: RecruitSlot) {
        whileNotMatch(slot.isAvailable) {
            tap(1221, 41).sleep()
        }

        await(slot.isAvailable)
    }

}