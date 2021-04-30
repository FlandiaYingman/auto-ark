package top.anagke.auto_ark.ark

import kotlinx.serialization.Serializable
import top.anagke.auto_ark.adb.Ops
import top.anagke.auto_ark.adb.OpsContext
import top.anagke.auto_ark.adb.Tmpl
import top.anagke.auto_ark.adb.assert
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.back
import top.anagke.auto_ark.adb.delay
import top.anagke.auto_ark.adb.match
import top.anagke.auto_ark.adb.ops
import top.anagke.auto_ark.adb.tap
import top.anagke.auto_ark.img.Img
import top.anagke.auto_ark.img.crop
import top.anagke.auto_ark.img.ocr
import java.awt.Rectangle

@Serializable
data class RecruitProps(
    val star4Tags: List<List<String>> = listOf(
        listOf("特种干员"),
        listOf("支援"),
        listOf("削弱"),
        listOf("快速复活"),
        listOf("位移"),
        listOf("近卫干员", "减速"),
        listOf("狙击干员", "生存"),
        listOf("狙击干员", "减速"),
        listOf("术师干员", "减速"),
        listOf("先锋干员", "治疗"),
        listOf("治疗", "费用回复"),
        listOf("输出", "减速"),
        listOf("生存", "远程位"),
        listOf("群攻", "减速"),
        listOf("减速", "近战位"),
    ),
    val star5Tags: List<List<String>> = listOf(
        listOf("控场"),
        listOf("爆发"),
        listOf("召唤"),
        listOf("近卫干员", "防护"),
        listOf("重装干员", "输出"),
        listOf("重装干员", "生存"),
        listOf("重装干员", "位移"),
        listOf("辅助干员", "输出"),
        listOf("辅助干员", "削弱"),
        listOf("术师干员", "治疗"),
        listOf("特种干员", "输出"),
        listOf("特种干员", "生存"),
        listOf("特种干员", "减速"),
        listOf("特种干员", "削弱"),
        listOf("先锋干员", "支援"),
        listOf("治疗", "输出"),
        listOf("治疗", "减速"),
        listOf("支援", "费用回复"),
        listOf("输出", "防护"),
        listOf("输出", "位移"),
        listOf("生存", "防护"),
        listOf("群攻", "削弱"),
        listOf("防护", "位移"),
        listOf("减速", "位移"),
        listOf("削弱", "快速复活"),
        listOf("削弱", "近战位"),
    ),
    val star6Tags: List<List<String>> = listOf(
        listOf("高级资深干员"),
    )
)

private val props = arkProps.recruitProps


// 公开招募界面
val atRecruitSlotsScreen by template("atRecruitSlotsScreen.png")
// 公开招募开包
val awaitHrAvailable by template("awaitHrAvailable.png")
// 能够刷新TAG
val canRefreshTag by template("canRefreshTag.png", diff = 0.01)
val atRecruitScreen by template("atRecruitScreen.png")

// 公开招募1号栏位可用
val isRecruitSlot1Available by template("isRecruitSlot1Available.png", diff = 0.01)
val isRecruitSlot1Finished by template("isRecruitSlot1Finished.png", diff = 0.01)
val isRecruitSlot1Recruiting by template("isRecruitSlot1Recruiting.png", diff = 0.01)
// 公开招募2号栏位可用
val isRecruitSlot2Available by template("isRecruitSlot2Available.png", diff = 0.01)
val isRecruitSlot2Finished by template("isRecruitSlot1Finished.png", diff = 0.01)
val isRecruitSlot2Recruiting by template("isRecruitSlot1Recruiting.png", diff = 0.01)
// 公开招募3号栏位可用
val isRecruitSlot3Available by template("isRecruitSlot3Available.png", diff = 0.01)
val isRecruitSlot3Finished by template("isRecruitSlot1Finished.png", diff = 0.01)
val isRecruitSlot3Recruiting by template("isRecruitSlot1Recruiting.png", diff = 0.01)
// 公开招募4号栏位可用
val isRecruitSlot4Available by template("isRecruitSlot4Available.png", diff = 0.01)
val isRecruitSlot4Finished by template("isRecruitSlot1Finished.png", diff = 0.01)
val isRecruitSlot4Recruiting by template("isRecruitSlot1Recruiting.png", diff = 0.01)

/**
 * 自动化公开招募。
 *
 * 其策略为：
 * - 当发现六星TAG组合时，保持原样。
 * - 当发现五星TAG组合时，招募。
 * - 当发现四星TAG组合时，招募。
 *
 * 开始于：主界面。
 * 结束于：主界面。
 */
fun autoRecruitment(): Ops {
    return ops {
        assert(atMainScreen)
        tap(1002, 507)

        await(atRecruitSlotsScreen)

        RecruitSlot.values().forEach { slot ->
            do {
                val tmpl = match(slot.isAvailableTmpl, slot.isFinishedTmpl, slot.isRecruitingTmpl)
                when (tmpl) {
                    slot.isAvailableTmpl -> {
                        startRecruit(slot)
                    }
                    slot.isFinishedTmpl -> {
                        finishRecruit(slot)
                    }
                    slot.isRecruitingTmpl -> {
                        expediteRecruit(slot)
                    }
                }
            } while (tmpl != null)
        }

        back()
        await(atMainScreen)
    }
}

private fun OpsContext.startRecruit(slot: RecruitSlot, refreshed: Boolean = false): Any = device(ops {
    if (!refreshed) {
        when (slot) {
            RecruitSlot.SLOT1 -> tap(475, 380)
            RecruitSlot.SLOT2 -> tap(1101, 380)
            RecruitSlot.SLOT3 -> tap(505, 664)
            RecruitSlot.SLOT4 -> tap(1103, 661)
        }
        delay(1000)
    }

    val recResult = solveRec(parseRec(device.cap())) ?: RecResult()
    if (recResult.tier == 0 && match(canRefreshTag)) {
        tap(972, 408) //刷新TAG
        tap(877, 508) //确认刷新TAG
        startRecruit(slot, refreshed = true)
        return@ops Unit
    }
    if (recResult.manual) {
        back()
        await(atRecruitSlotsScreen)
    } else {
        val (tag1, tag2, tag3, tag4, tag5) = recResult
        if (tag1) tap(438, 383)
        if (tag2) tap(613, 379)
        if (tag3) tap(771, 383)
        if (tag4) tap(452, 456)
        if (tag5) tap(601, 451)

        delay(1000)
        tap(450, 300) //增加时限到”9：00：00“
        tap(977, 588, delay = 1000) // 开始招募
        await(atRecruitSlotsScreen, atRecruitScreen).let {
            if (it == atRecruitScreen) {
                back()
                await(atRecruitSlotsScreen)
            }
        }
    }
})

private fun OpsContext.expediteRecruit(slot: RecruitSlot): Any = device(ops {
    assert(slot.isRecruitingTmpl)
    when (slot) {
        RecruitSlot.SLOT1 -> tap(475, 380)
        RecruitSlot.SLOT2 -> tap(1101, 380)
        RecruitSlot.SLOT3 -> tap(505, 664)
        RecruitSlot.SLOT4 -> tap(1103, 661)
    }
    tap(955, 518, delay = 1000)
    await(slot.isRecruitingTmpl, slot.isFinishedTmpl)
})

private fun OpsContext.finishRecruit(slot: RecruitSlot): Any = device(ops {
    assert(slot.isFinishedTmpl)
    when (slot) {
        RecruitSlot.SLOT1 -> tap(475, 380)
        RecruitSlot.SLOT2 -> tap(1101, 380)
        RecruitSlot.SLOT3 -> tap(505, 664)
        RecruitSlot.SLOT4 -> tap(1103, 661)
    }
    delay(1000)
    tap(1221, 41, delay = 2000)
    tap(1221, 41)
    await(slot.isAvailableTmpl)
})


data class RecTags(
    val tag1: String,
    val tag2: String,
    val tag3: String,
    val tag4: String,
    val tag5: String,
) {
    operator fun contains(tag: String): Boolean {
        return tag1 == tag || tag2 == tag || tag3 == tag || tag4 == tag || tag5 == tag
    }

    fun matches(tags: List<String>): RecResult? {
        if (tags.all { it in this }) {
            return RecResult(
                tags.any { it == tag1 },
                tags.any { it == tag2 },
                tags.any { it == tag3 },
                tags.any { it == tag4 },
                tags.any { it == tag5 },
            )
        } else {
            return null
        }
    }
}

data class RecResult(
    val tag1: Boolean = false,
    val tag2: Boolean = false,
    val tag3: Boolean = false,
    val tag4: Boolean = false,
    val tag5: Boolean = false,
    val tier: Int = 0,
    val manual: Boolean = false
)

enum class RecruitSlot(
    val isAvailableTmpl: Tmpl,
    val isRecruitingTmpl: Tmpl,
    val isFinishedTmpl: Tmpl,
) {
    SLOT1(
        isRecruitSlot1Available,
        isRecruitSlot1Recruiting,
        isRecruitSlot1Finished
    ),
    SLOT2(
        isRecruitSlot2Available,
        isRecruitSlot2Recruiting,
        isRecruitSlot2Finished
    ),
    SLOT3(
        isRecruitSlot3Available,
        isRecruitSlot3Recruiting,
        isRecruitSlot3Finished
    ),
    SLOT4(
        isRecruitSlot4Available,
        isRecruitSlot4Recruiting,
        isRecruitSlot4Finished
    ),
}


fun parseRec(img: Img): RecTags {
    val tags = listOf(
        Rectangle(375, 360, 144, 46),
        Rectangle(542, 360, 144, 46),
        Rectangle(709, 360, 144, 46),
        Rectangle(375, 432, 144, 46),
        Rectangle(542, 432, 144, 46),
    ).map {
        ocr(crop(img, it))
    }
    return RecTags(tags[0], tags[1], tags[2], tags[3], tags[4])
}

fun solveRec(tags: RecTags): RecResult? {
    props.forEach {
        val result = tags.matches(it)
        if (result != null) {
            return RecResult(result.tag1, result.tag2, result.tag3, result.tag4, result.tag5, manual = true, tier = 6)
        }
    }
    star5Tags.forEach {
        val result = tags.matches(it)
        if (result != null) {
            return RecResult(result.tag1, result.tag2, result.tag3, result.tag4, result.tag5, tier = 5)
        }
    }
    star4Tags.forEach {
        val result = tags.matches(it)
        if (result != null) {
            return RecResult(result.tag1, result.tag2, result.tag3, result.tag4, result.tag5, tier = 4)
        }
    }
    return null
}

