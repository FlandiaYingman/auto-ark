package dev.flandia.ark.recruit

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import dev.flandia.ark.AutoArk

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
    ) : Comparable<RecruitOperator> {

        fun canRecruitBy(tags: List<String>): Boolean {
            if (this.rarity == 5 && "高级资深干员" !in tags) return false
            if (this.rarity == 0 && "支援机械" !in tags) return false
            if (this.rarity == 1) return false
            return this.tags.containsAll(tags)
        }

        override fun compareTo(other: RecruitOperator): Int = when {
            (this.rarity == 5 || other.rarity == 5) -> compareValues(other.rarity, this.rarity)
            (this.rarity == 0 || other.rarity == 0) -> compareValues(this.rarity, other.rarity)
            else -> compareValues(this.rarity, other.rarity)
        }


        override fun toString(): String {
            return name
        }


    }

    private val operators: List<Operator> = run {
        val json = AutoArk::class.java.getResource("operator_data.json")!!.readText()
        val type = object : TypeToken<List<Operator>>() {}.type
        Gson().fromJson(json, type)
    }

    private val recruitOperators: List<RecruitOperator> = operators
        .filter(Operator::isRecruitOperator)
        .map(Operator::toRecruitOperator)

    private fun calculate(tags: List<String>): Map<List<String>, List<RecruitOperator>> {
        return tags.combinations(3)
            .shuffled()
            .associateWith { combTags ->
                recruitOperators
                    .filter { op -> op.canRecruitBy(combTags) }
                    .shuffled()
                    .sorted()
            }
            .filterValues { it.isNotEmpty() }

    }

    fun calculateBest(tags: List<String>): Pair<List<String>, List<RecruitOperator>> {
        val calculate = calculate(tags)
        val resultKey = calculate
            .mapValues { (_, v) -> v.first() }
            .maxBy { (_, v) -> v.rarity }
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

