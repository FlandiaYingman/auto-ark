package top.anagke.auto_ark.ark

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.Ops
import top.anagke.auto_ark.adb.OpsContext
import top.anagke.auto_ark.adb.Tmpl
import top.anagke.auto_ark.adb.assert
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.back
import top.anagke.auto_ark.adb.delay
import top.anagke.auto_ark.adb.match
import top.anagke.auto_ark.adb.matched
import top.anagke.auto_ark.adb.matchedAny
import top.anagke.auto_ark.adb.ops
import top.anagke.auto_ark.adb.tap
import top.anagke.auto_ark.adb.which
import top.anagke.auto_ark.ark.RecruitSlotStatus.*
import top.anagke.auto_ark.autoProps
import top.anagke.auto_ark.img.Img
import top.anagke.auto_ark.img.crop
import top.anagke.auto_ark.img.ocr
import java.awt.Rectangle


@Serializable
data class RecruitProps(
    val retain6SOperators: Boolean = true,
    val retain5SOperators: Boolean = true,
    val retain4SOperators: Boolean = false,
)

private val props = arkProps.recruitProps
private val log = KotlinLogging.logger {}


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

private enum class RecruitSlotStatus {
    AVAILABLE,
    RECRUITING,
    COMPLETED,
}

fun main() {
    Device(autoProps.adbHost, autoProps.adbPort)(autoRecruit())
}


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
fun autoRecruit(): Ops {
    return ops {
        assert(atMainScreen)

        tap(1002, 507)
        await(atRecruitSlotsScreen)

        var emptyRecruitmentPermit = false
        var emptyExpeditedPlan = false
        RecruitSlot.values().forEach { slot ->
            var previousStatus: RecruitSlotStatus? = null
            do {
                val matched = which(slot.isAvailable, slot.isCompleted, slot.isRecruiting)
                when (slot) {
                    RecruitSlot.SLOT1 -> tap(475, 380)
                    RecruitSlot.SLOT2 -> tap(1101, 380)
                    RecruitSlot.SLOT3 -> tap(505, 664)
                    RecruitSlot.SLOT4 -> tap(1103, 661)
                }
                delay(1000)
                if (matched == slot.isAvailable) {
                    if (emptyRecruitmentPermit) {
                        break
                    }
                    if (previousStatus == AVAILABLE) {
                        log.info { "检测到招募许可不足，放弃招募" }
                        emptyRecruitmentPermit = true
                        break
                    }
                    startRecruit(slot)
                    previousStatus = AVAILABLE
                }
                if (matched == slot.isRecruiting) {
                    if (emptyExpeditedPlan) {
                        break
                    }
                    if (match(slot.isRecruiting)) {
                        log.info { "检测到加急许可不足，放弃加急" }
                        emptyExpeditedPlan = true
                        break
                    }
                    expediteRecruit(slot)
                    previousStatus = RECRUITING
                }
                if (matched == slot.isCompleted) {
                    completeRecruit(slot)
                    previousStatus = COMPLETED
                }
            } while (matchedAny())
        }

        back()
        await(atMainScreen)
    }
}

private fun OpsContext.startRecruit(slot: RecruitSlot) {
    val parsed = parse(device.cap())
    val solved = solve(parsed.keys.toList())
    when {
        solved.maxRarity() == 6 && props.retain6SOperators -> {
            log.info { "检测到可公开招募六星干员！" }
            solved.operators6S.print()
        }
        solved.maxRarity() == 5 && props.retain5SOperators -> {
            log.info { "检测到可公开招募五星干员！" }
            solved.operators5S.print()
        }
        solved.maxRarity() == 4 && props.retain4SOperators -> {
            log.info { "检测到可公开招募四星干员！" }
            solved.operators4S.print()
        }
        else -> {
            val tagCombination = when {
                solved.operators6S.isNotEmpty() -> solved.operators6S.keys.shuffled()[0]
                solved.operators5S.isNotEmpty() -> solved.operators5S.keys.shuffled()[0]
                solved.operators4S.isNotEmpty() -> solved.operators4S.keys.shuffled()[0]
                else -> listOf()
            }
            val tags = tagCombination.map { parsed[it] }
            if (RecruitTag.TAG1 in tags) tap(438, 383)
            if (RecruitTag.TAG2 in tags) tap(613, 379)
            if (RecruitTag.TAG3 in tags) tap(771, 383)
            if (RecruitTag.TAG4 in tags) tap(452, 456)
            if (RecruitTag.TAG5 in tags) tap(601, 451)
            delay(1000)
        }
    }
    if (solved.maxRarity() == 0 && match(canRefreshTag)) {
        tap(972, 408) //刷新TAG
        tap(877, 508) //确认刷新TAG
        delay(2000)
        startRecruit(slot)
        return
    }

    tap(450, 300) //增加时限到”9：00：00“
    tap(977, 588, delay = 1000) // 开始招募
    await(atRecruitSlotsScreen, atRecruitScreen)
    if (matched(atRecruitScreen)) {
        back()
        await(atRecruitSlotsScreen)
    }
}

private fun OpsContext.expediteRecruit(slot: RecruitSlot) {
    tap(955, 518, delay = 1000)
    await(slot.isRecruiting, slot.isCompleted)
}

private fun OpsContext.completeRecruit(slot: RecruitSlot) {
    tap(1221, 41, delay = 2000)
    tap(1221, 41, delay = 1000)
    await(slot.isAvailable)
}


private val recruitOperators: List<RecruitOperator> = run {
    Gson().fromJson<List<RecruitOperator>>(
        ArkRes("recruit_data.json")!!.readText(),
        object : TypeToken<List<RecruitOperator>>() {}.type
    ).filter {
        it.approach.contains("公开招募")
    }
}

private val recruitTagOperatorMap: Map<String, Set<RecruitOperator>> = run {
    recruitOperators.flatMap { it.tag }.distinct().associateWith { tag ->
        recruitOperators.filter { it.tag.contains(tag) }.toSet()
    }
}

private data class RecruitOperator(
    val cn: String,
    val rarity: Int,
    val tag: List<String>,
    val approach: List<String>,
)

private enum class RecruitTag {
    TAG1,
    TAG2,
    TAG3,
    TAG4,
    TAG5
}

private data class RecruitResult(
    val operators6S: Map<List<String>, List<RecruitOperator>>,
    val operators5S: Map<List<String>, List<RecruitOperator>>,
    val operators4S: Map<List<String>, List<RecruitOperator>>,
) {

    fun maxRarity(): Int {
        if (operators6S.isNotEmpty()) return 6
        if (operators5S.isNotEmpty()) return 5
        if (operators4S.isNotEmpty()) return 4
        return 0
    }

}

private fun parse(img: Img): Map<String, RecruitTag> {
    val tags = listOf(
        Rectangle(375, 360, 144, 46),
        Rectangle(542, 360, 144, 46),
        Rectangle(709, 360, 144, 46),
        Rectangle(375, 432, 144, 46),
        Rectangle(542, 432, 144, 46),
    ).map {
        ocr(crop(img, it))
    }
    return listOf(
        tags[0] to RecruitTag.TAG1,
        tags[1] to RecruitTag.TAG2,
        tags[2] to RecruitTag.TAG3,
        tags[3] to RecruitTag.TAG4,
        tags[4] to RecruitTag.TAG5,
    ).toMap()
}

private fun solve(tags: List<String>): RecruitResult {
    val tagCombinations = (tags + null + null)
        .combinations(3)
        .map { combination -> combination.filterNotNull() }
        .distinct()
    val tagCombinationOperators = tagCombinations.map { combination ->
        val operators = combination.map { tag ->
            recruitTagOperatorMap[tag] ?: emptySet()
        }.map { operators ->
            if ("高级资深干员" !in tags) {
                operators.filter { it.rarity != 5 }
            } else {
                operators
            }
        }.reduceOrNull { a, b ->
            a intersect b
        } ?: emptySet()
        combination to operators
    }

    val operators6S = tagCombinationOperators.filter { (_, operators) ->
        operators.isNotEmpty() && operators.all { it.rarity >= 5 } && operators.any { it.rarity == 5 }
    }.associate { (combination, operators) ->
        combination to operators.toList()
    }
    val operators5S = tagCombinationOperators.filter { (_, operators) ->
        operators.isNotEmpty() && operators.all { it.rarity >= 4 } && operators.any { it.rarity == 4 }
    }.associate { (combination, operators) ->
        combination to operators.toList()
    }
    val operators4S = tagCombinationOperators.filter { (_, operators) ->
        operators.isNotEmpty() && operators.all { it.rarity >= 3 } && operators.any { it.rarity == 3 }
    }.associate { (combination, operators) ->
        combination to operators.toList()
    }

    return RecruitResult(operators6S, operators5S, operators4S)
}

//https://www.baeldung.com/java-combinations-algorithm
private fun <T> List<T>.combinations(r: Int): List<List<T>> {
    val n = this.size
    val combinations: MutableList<IntArray> = ArrayList()
    val combination = IntArray(r)

    // initialize with lowest lexicographic combination
    for (i in 0 until r) {
        combination[i] = i
    }
    while (combination[r - 1] < n) {
        combinations.add(combination.clone())

        // generate next combination in lexicographic order
        var t = r - 1
        while (t != 0 && combination[t] == n - r + t) {
            t--
        }
        combination[t]++
        for (i in t + 1 until r) {
            combination[i] = combination[i - 1] + 1
        }
    }
    return combinations.map { arr ->
        arr.map { item -> this[item] }
    }
}

private fun Map<List<String>, List<RecruitOperator>>.print() {
    this.forEach { (tags, possibleOperators) ->
        val names = possibleOperators.map { it.cn }
        log.info { "$tags: $names" }
    }
}
