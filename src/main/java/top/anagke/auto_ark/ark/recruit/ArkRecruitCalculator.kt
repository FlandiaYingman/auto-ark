@file:Suppress("UNUSED_DESTRUCTURED_PARAMETER_ENTRY")

package top.anagke.auto_ark.ark.recruit

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import top.anagke.auto_ark.ark.ArkRes

object ArkRecruitCalculator {

    private data class Operator(
        val cn: String,
        val position: String,
        @SerializedName("class")
        val clazz: String,
        val rarity: String,
        val approach: List<String>,
        val tag: List<String>,
    ) {

        fun isRecruitOperator() = approach.contains("公开招募")

        fun toRecruitOperator() = RecruitOperator(
            cn,
            rarity.toInt(),
            tag + tagsOfPosition() + tagsOfClass() + tagsOfRarity(),
        )

        fun tagsOfPosition(): List<String> {
            return listOf(position)
        }

        fun tagsOfClass(): List<String> {
            return listOf("${clazz}干员")
        }

        fun tagsOfRarity(): List<String> {
            return when (rarity) {
                "4" -> listOf("资深干员")
                "5" -> listOf("高级资深干员")
                else -> emptyList()
            }
        }

    }

    data class RecruitOperator(
        val name: String,
        val rarity: Int,
        val tags: List<String>,
    ) {

        fun canRecruitBy(tags: List<String>): Boolean {
            if (this.rarity == 5 && "高级资深干员" !in tags) return false
            return this.tags.containsAll(tags)
        }

        override fun toString(): String {
            return name
        }

    }

    private val operators: List<Operator> = run {
        val json = ArkRes("operator_data.json")!!.readText()
        val type = object : TypeToken<List<Operator>>() {}.type
        Gson().fromJson(json, type)
    }

    private val recruitOperators: List<RecruitOperator> = operators
        .filter(Operator::isRecruitOperator)
        .map(Operator::toRecruitOperator)


    fun calculate(tags: List<String>): Map<List<String>, List<RecruitOperator>> {
        val result = tags.combinations(3)
            .associateWith {
                recruitOperators
                    .filter { op -> op.canRecruitBy(it) }
                    .filter { op -> op.rarity > 1 } //暂时不支持二星（支援机械）
                    .sortedBy(RecruitOperator::rarity)
//                TODO("支持二星（支援机械）")
            }
            .filterValues { it.isNotEmpty() }
        return result
    }

    fun calculateBest(tags: List<String>): Pair<List<String>, List<RecruitOperator>> {
        val calculate = calculate(tags)
        val resultKey = calculate
            .mapValues { (tagCombination, possibleOperators) -> possibleOperators.minOf { it.rarity } }
            .maxByOrNull { (tagCombination, minimumRarity) -> minimumRarity }!!
            .key
        return resultKey to calculate[resultKey]!!
    }


    private fun <T> List<T>.combinations(len: Int = Int.MAX_VALUE): List<List<T>> {
        val result = ArrayList<List<T>>()
        fun func(prefix: List<T>, rest: List<T>) {
            for (i in rest.indices) {
                val merge = prefix + rest[i]
                if (merge.size > len) break
                result += merge
                func(merge, if (i + 1 < rest.size) rest.subList(i + 1, rest.size) else emptyList())
            }
        }
        func(listOf(), this)
        return result
    }

}

