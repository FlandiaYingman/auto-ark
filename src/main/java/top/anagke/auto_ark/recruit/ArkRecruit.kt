package top.anagke.auto_ark.recruit

import kotlinx.serialization.Serializable
import mu.KotlinLogging
import top.anagke.auto_android.img.Tmpl
import top.anagke.auto_android.img.ocrTesseract
import top.anagke.auto_android.util.Rect
import top.anagke.auto_ark.AutoArk
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.assert
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.match
import top.anagke.auto_ark.adb.matched
import top.anagke.auto_ark.adb.sleep
import top.anagke.auto_ark.adb.whileNotMatch
import top.anagke.auto_ark.appConfig
import top.anagke.auto_ark.atMainScreen
import top.anagke.auto_ark.jumpOut
import top.anagke.auto_ark.recruit.ArkRecruitCalculator.RecruitOperator
import top.anagke.auto_ark.recruit.RecruitTag.*
import top.anagke.auto_ark.template
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
private val isRecruitSlot1Available = template("recruit/isRecruitSlot1Available.png", diff = 0.02)
private val isRecruitSlot1Completed = template("recruit/isRecruitSlot1Completed.png", diff = 0.02)
private val isRecruitSlot1Recruiting = template("recruit/isRecruitSlot1Recruiting.png", diff = 0.02)
// 公开招募2号栏位可用
private val isRecruitSlot2Available = template("recruit/isRecruitSlot2Available.png", diff = 0.02)
private val isRecruitSlot2Completed = template("recruit/isRecruitSlot2Completed.png", diff = 0.02)
private val isRecruitSlot2Recruiting = template("recruit/isRecruitSlot2Recruiting.png", diff = 0.02)
// 公开招募3号栏位可用
private val isRecruitSlot3Available = template("recruit/isRecruitSlot3Available.png", diff = 0.02)
private val isRecruitSlot3Completed = template("recruit/isRecruitSlot3Completed.png", diff = 0.02)
private val isRecruitSlot3Recruiting = template("recruit/isRecruitSlot3Recruiting.png", diff = 0.02)
// 公开招募4号栏位可用
private val isRecruitSlot4Available = template("recruit/isRecruitSlot4Available.png", diff = 0.02)
private val isRecruitSlot4Completed = template("recruit/isRecruitSlot4Completed.png", diff = 0.02)
private val isRecruitSlot4Recruiting = template("recruit/isRecruitSlot4Recruiting.png", diff = 0.02)


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

private enum class RecruitTag(
    val screenRect: Rect,
) {
    TAG1(Rect(375, 360, 144, 46)),
    TAG2(Rect(542, 360, 144, 46)),
    TAG3(Rect(709, 360, 144, 46)),
    TAG4(Rect(375, 432, 144, 46)),
    TAG5(Rect(542, 432, 144, 46)),
}


class ArkRecruit(
    private val device: Device,
    private val config: RecruitConfig,
) {

    private val logger = KotlinLogging.logger { }

    private var hasRecruitmentPermit = true

    private var hasExpeditedPlan = true

    private val skippingSlotList: MutableList<RecruitSlot> = mutableListOf()


    fun auto() = device.apply {
        logger.info { "运行模块：公开招募" }
        assert(atMainScreen)

        tap(1000, 510) //公开招募
        await(atRecruitSlotsScreen)

        RecruitSlot.values().forEach { slot ->
            autoSlot(slot)
        }

        jumpOut()
        logger.info { "结束模块：公开招募" }
    }

    private fun autoSlot(slot: RecruitSlot) = device.apply {
        while (true) {
            if (slot in skippingSlotList) break
            val slotStatus = assert(slot.isAvailable, slot.isRecruiting, slot.isCompleted)
            logger.info { "检查槽位：${slot.name}，状态：${slotStatus.name}，存在招募许可：${hasRecruitmentPermit}，存在加急许可：$hasExpeditedPlan" }
            when (slotStatus) {
                slot.isAvailable -> if (hasRecruitmentPermit) startRecruit(slot) else break
                slot.isRecruiting -> if (hasExpeditedPlan) expediteRecruit(slot) else break
                slot.isCompleted -> completeRecruit(slot)
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

    private fun Device.parseTags(): Map<RecruitTag, String> {
        val cap = cap()
        return RecruitTag.values().toList()
            .parallelStream()
            .map { it to ocrTesseract(cap.crop(it.screenRect).invert()) }
            .collect(Collectors.toList())
            .toMap()
    }


    private fun startRecruit(slot: RecruitSlot) = device.apply {
        logger.info { "开始招募槽位：${slot.name}" }
        val exitRecruit = {
            back()
            await(atRecruitSlotsScreen)
        }
        tapSlot(slot)
        while (true) {
            val tags = parseTags()
            val (tagCombination, possibleOperators) = ArkRecruitCalculator.calculateBest(tags.values.toList())
            logger.info { "标签：$tags，最佳标签组合：$tagCombination，干员列表：$possibleOperators" }

            val minimumRarity = possibleOperators.minOf(RecruitOperator::rarity)
            logger.info { "最低可能星级：$minimumRarity" }
            if (minimumRarity >= 4) {
                logger.info { "最低可能星级大于等于五星，退出" }
                skippingSlotList += slot
                exitRecruit()
                break
            }
            if (minimumRarity <= 2 && match(canRefreshTag)) {
                logger.info { "最低可能星级小于等于三星且可刷新，刷新" }
                tap(972, 408) //刷新TAG
                tap(877, 508) //确认刷新TAG
                sleep()
                continue
            }

            if (tags[TAG1] in tagCombination) tap(438, 383)
            if (tags[TAG2] in tagCombination) tap(613, 379)
            if (tags[TAG3] in tagCombination) tap(771, 383)
            if (tags[TAG4] in tagCombination) tap(452, 456)
            if (tags[TAG5] in tagCombination) tap(601, 451)

            tap(450, 300) //增加时限到”9：00：00“
            tap(977, 588) // 开始招募
            sleep()

            await(atRecruitSlotsScreen, atRecruitScreen)
            if (matched(atRecruitScreen)) {
                hasRecruitmentPermit = false
                logger.info { "招募许可不足，完成招募槽位：${slot.name}" }
                exitRecruit()
                break
            } else {
                logger.info { "开始招募槽位：${slot.name}，完毕" }
                break
            }
        }
    }

    private fun expediteRecruit(slot: RecruitSlot) = device.apply {
        logger.info { "立即招募槽位：${slot.name}" }
        tapSlot(slot)
        if (match(slot.isRecruiting)) {
            hasExpeditedPlan = false
            logger.info { "加急许可不足，跳过加急槽位：${slot.name}" }
        } else {
            tap(955, 518).sleep()
            await(slot.isCompleted)
            logger.info { "立即招募槽位：${slot.name}，完毕" }
        }
    }

    private fun completeRecruit(slot: RecruitSlot) = device.apply {
        logger.info { "完成招募：${slot.name}" }
        tapSlot(slot)
        whileNotMatch(slot.isAvailable) {
            tap(1221, 41).sleep()
        }
        await(slot.isAvailable)
        logger.info { "完成招募：${slot.name}，完毕" }
    }

}

fun main() {
    AutoArk(appConfig).autoRecruit()
}