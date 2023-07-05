package top.anagke.auto_ark.recruit


import org.tinylog.kotlin.Logger
import top.anagke.auto_android.device.*
import top.anagke.auto_android.img.Tmpl
import top.anagke.auto_android.img.ocr
import top.anagke.auto_android.util.Rect
import top.anagke.auto_ark.*
import top.anagke.auto_ark.recruit.RecruitTag.*
import top.anagke.auto_ark.recruit.RecruitTemplates.公开招募界面
import top.anagke.auto_ark.recruit.RecruitTemplates.公开招募面板
import top.anagke.auto_ark.recruit.RecruitTemplates.可刷新标签
import top.anagke.auto_ark.recruit.RecruitTemplates.招募槽1完成
import top.anagke.auto_ark.recruit.RecruitTemplates.招募槽1招募中
import top.anagke.auto_ark.recruit.RecruitTemplates.招募槽1空闲
import top.anagke.auto_ark.recruit.RecruitTemplates.招募槽2完成
import top.anagke.auto_ark.recruit.RecruitTemplates.招募槽2招募中
import top.anagke.auto_ark.recruit.RecruitTemplates.招募槽2空闲
import top.anagke.auto_ark.recruit.RecruitTemplates.招募槽3完成
import top.anagke.auto_ark.recruit.RecruitTemplates.招募槽3招募中
import top.anagke.auto_ark.recruit.RecruitTemplates.招募槽3空闲
import top.anagke.auto_ark.recruit.RecruitTemplates.招募槽4完成
import top.anagke.auto_ark.recruit.RecruitTemplates.招募槽4招募中
import top.anagke.auto_ark.recruit.RecruitTemplates.招募槽4空闲
import java.util.stream.Collectors

private enum class RecruitSlot(
    val isAvailable: Tmpl,
    val isRecruiting: Tmpl,
    val isCompleted: Tmpl,
) {
    SLOT1(
        招募槽1空闲,
        招募槽1招募中,
        招募槽1完成
    ),
    SLOT2(
        招募槽2空闲,
        招募槽2招募中,
        招募槽2完成
    ),
    SLOT3(
        招募槽3空闲,
        招募槽3招募中,
        招募槽3完成
    ),
    SLOT4(
        招募槽4空闲,
        招募槽4招募中,
        招募槽4完成
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
    auto: AutoArk,
) : ArkModule(auto) {


    private var hasRecruitmentPermit = true

    private var hasExpeditedPlan = true

    private val skippingSlotList: MutableList<RecruitSlot> = mutableListOf()

    override val name: String = "公开招募模块"

    override fun run() = device.run {
        Logger.info("运行模块：公开招募")
        assert(主界面)

        tap(1000, 510) //公开招募
        await(公开招募界面)

        RecruitSlot.values().forEach { slot ->
            autoSlot(slot)
        }

        resetInterface()
        Logger.info("结束模块：公开招募")
    }

    private fun autoSlot(slot: RecruitSlot) = device.apply {
        while (true) {
            if (slot in skippingSlotList) break
            val slotStatus = assert(slot.isAvailable, slot.isRecruiting, slot.isCompleted)
            Logger.info("检查槽位：${slot.name}，状态：${slotStatus.name}，存在招募许可：${hasRecruitmentPermit}，存在加急许可：$hasExpeditedPlan")
            when (slotStatus) {
                slot.isAvailable -> if (hasRecruitmentPermit) startRecruit(slot) else break
                slot.isRecruiting -> break
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
            .map { it to ocr(cap.crop(it.screenRect).invert()) }
            .collect(Collectors.toList())
            .toMap()
    }


    private fun startRecruit(slot: RecruitSlot) = device.apply {
        Logger.info("开始招募槽位：${slot.name}")
        val exitRecruit = {
            back()
            await(公开招募界面)
        }
        tapSlot(slot)
        while (true) {
            val tags = parseTags()
            val (tagCombination, possibleOperators) = ArkRecruitCalculator.calculateBest(tags.values.toList())
            Logger.info("标签：$tags，最佳标签组合：$tagCombination，干员列表：$possibleOperators")

            val mostPossibleRarity = possibleOperators.min().rarity
            Logger.info("最可能星级：$mostPossibleRarity")
            if (mostPossibleRarity == 5) {
                Logger.info("最可能星级等于六星，退出")
                skippingSlotList += slot
                exitRecruit()
                break
            }
            if ((mostPossibleRarity == 2 || mostPossibleRarity == 1) && match(可刷新标签)) {
                Logger.info("最低可能星级等于三星且可刷新，刷新")
                tap(972, 408).nap() //刷新TAG
                tap(877, 508).sleep() //确认刷新TAG
                continue
            }

            if (tags[TAG1] in tagCombination) tap(438, 383)
            if (tags[TAG2] in tagCombination) tap(613, 379)
            if (tags[TAG3] in tagCombination) tap(771, 383)
            if (tags[TAG4] in tagCombination) tap(452, 456)
            if (tags[TAG5] in tagCombination) tap(601, 451)

            if (mostPossibleRarity == 1) {
                repeat(3) { tap(449, 152) }
                repeat(1) { tap(617, 297) }
            } else {
                tap(450, 300) //增加时限到“9：00：00”
            }
            tap(977, 588) // 开始招募
            sleep()

            await(公开招募界面, 公开招募面板)
            if (matched(公开招募面板)) {
                hasRecruitmentPermit = false
                Logger.info("招募许可不足，完成招募槽位：${slot.name}")
                exitRecruit()
                break
            } else {
                Logger.info("开始招募槽位：${slot.name}，完毕")
                break
            }
        }
        if (hasExpeditedPlan && match(slot.isRecruiting)) expediteRecruit(slot)
    }

    private fun expediteRecruit(slot: RecruitSlot) = device.apply {
        Logger.info("立即招募槽位：${slot.name}")
        tapSlot(slot)
        if (match(slot.isRecruiting)) {
            hasExpeditedPlan = false
            Logger.info("加急许可不足，跳过加急槽位：${slot.name}")
        } else {
            tap(955, 518).sleep()
            await(slot.isCompleted)
            Logger.info("立即招募槽位：${slot.name}，完毕")
        }
    }

    private fun completeRecruit(slot: RecruitSlot) = device.apply {
        Logger.info("完成招募：${slot.name}")
        tapSlot(slot)
        whileNotMatch(slot.isAvailable) {
            tap(1221, 41).sleep()
        }
        await(slot.isAvailable)
        Logger.info("完成招募：${slot.name}，完毕")
    }

}

fun main() {
    ArkRecruit(App.defaultAutoArk()).run()
}