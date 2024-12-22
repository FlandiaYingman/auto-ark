package dev.flandia.ark.recruit

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ArkRecruitCalculator {

    val recuritableOperators = run {
        // From gacha_table.json
        val operators =
            "<@rc.eml>Lancet-2</> / <@rc.eml>Castle-3</> / <@rc.eml>THRM-EX</> / <@rc.eml>正义骑士号</> / <@rc.eml>Friston-3</> / <@rc.eml>PhonoR-0</>\r\n--------------------\r\n★★\\n<@rc.eml>夜刀</> / <@rc.eml>黑角</> / <@rc.eml>巡林者</> / <@rc.eml>杜林</> / <@rc.eml>12F</>\r\n--------------------\r\n★★★\\n<@rc.eml>安德切尔</> / 芬 / 香草 / 翎羽 / 玫兰莎 / 米格鲁 / 克洛丝 / 炎熔 / 芙蓉 / 安赛尔 / 史都华德 / 梓兰 / 空爆 / 月见夜 / 泡普卡 / 斑点\r\n--------------------\r\n★★★★\\n<@rc.eml>艾丝黛尔</> / <@rc.eml>清流</> / 夜烟 / 远山 / 杰西卡 / 流星 / 白雪 / 清道夫 / 红豆 / 杜宾 / 缠丸 / 霜叶 / 慕斯 / 砾 / 暗索 / 末药 / 调香师 / 角峰 / 蛇屠箱 / 古米 / 地灵 / 阿消 / 猎蜂 / 格雷伊 / 苏苏洛 / 桃金娘 / 红云 / 梅 / 安比尔 / 宴 / 刻刀 / 波登可 / 卡达 / 孑 / 酸糖 / 芳汀 / 泡泡 / 杰克 / 松果\r\n--------------------\r\n★★★★★\\n<@rc.eml>因陀罗</> / <@rc.eml>火神</> / 白面鸮 / 凛冬 / 德克萨斯 / 幽灵鲨 / 蓝毒 / 白金 / 陨星 / 梅尔 / 赫默 / 华法琳 / 临光 / 红 / 雷蛇 / 可颂 / 普罗旺斯 / 守林人 / 崖心 / 初雪 / 真理 / 狮蝎 / 食铁兽 / 夜魔 / 诗怀雅 / 格劳克斯 / 星极 / 送葬人 / 槐琥 / 灰喉 / 苇草 / 布洛卡 / 吽 / 惊蛰 / 慑砂 / 巫恋 / 极境 / 石棉 / 月禾 / 莱恩哈特 / 断崖 / 安哲拉 / 贾维 / 蜜蜡 / 燧石 /四月 / 奥斯塔 / 絮雨 / 卡夫卡\r\n--------------------\r\n★★★★★★\\n能天使 / 推进之王 / 伊芙利特 / 闪灵 / 夜莺 / 星熊 / 塞雷娅 / 银灰 / 斯卡蒂 / 陈 / 黑 / 赫拉格 / 麦哲伦 / 莫斯提马 / 煌 / 阿 / 刻俄柏 / 风笛 / 傀影 / 温蒂 / 早露 / 铃兰 / 棘刺 / 森蚺 / 史尔特尔 / 瑕光 / 泥岩 / 山"
                .replace("<@rc.eml>", "")
                .replace("</>", "")
                .replace("-", "")
                .replace("★", "")
                .split(Regex("([/ \n\r]|\\\\n)+"))
                .filter { it.isNotBlank() }
        operators.toSet()
    }

    private data class Operator(
        val name: String,
        val position: String,
        val profession: String,
        val rarity: String,
        val tagList: List<String>?,
    ) {

        fun isRecuritable() = name in recuritableOperators

        fun toRecruitOperator() = RecruitOperator(
            name,
            rarity.removePrefix("TIER_").toInt(),
            (tagList ?: emptyList()) + tagsOfPosition() + tagsOfClass() + tagsOfRarity(),
        )

        fun tagsOfPosition(): List<String> {
            return when (position) {
                "MELEE" -> listOf("近战位")
                "RANGED" -> listOf("远程位")
                "NONE" -> emptyList()
                else -> throw IllegalStateException("unknown position: $position")
            }
        }

        fun tagsOfClass(): List<String> {
            return when (profession) {
                "PIONEER" -> listOf("先锋干员")
                "WARRIOR" -> listOf("近卫干员")
                "SNIPER" -> listOf("狙击干员")
                "MEDIC" -> listOf("医疗干员")
                "TANK" -> listOf("重装干员")
                "SUPPORT" -> listOf("辅助干员")
                "CASTER" -> listOf("术师干员")
                "SPECIAL" -> listOf("特种干员")
                else -> emptyList()
            }
        }

        fun tagsOfRarity(): List<String> {
            return when (rarity) {
                "TIER_5" -> listOf("资深干员")
                "TIER_6" -> listOf("高级资深干员")
                else -> if (rarity.startsWith("TIER_")) emptyList()
                else throw IllegalStateException("unknown rarity: $rarity")
            }
        }

    }

    data class RecruitOperator(
        val name: String,
        val rarity: Int,
        val tags: List<String>,
    ) : Comparable<RecruitOperator> {

        fun canRecruitBy(tags: List<String>): Boolean {
            if (this.rarity == 6 && "高级资深干员" !in tags) return false
            if (this.rarity == 1 && "支援机械" !in tags) return false
            if (this.rarity == 2) return false
            return this.tags.containsAll(tags)
        }

        override fun compareTo(other: RecruitOperator): Int = when {
            (this.rarity == 6 || other.rarity == 6) -> compareValues(this.rarity, other.rarity)
            (this.rarity == 1 || other.rarity == 1) -> compareValues(other.rarity, this.rarity)
            else -> compareValues(this.rarity, other.rarity)
        }

        override fun toString(): String {
            return name
        }

    }

    private val operators: List<Operator> = run {
        val json = ArkRecruitCalculator::class.java.getResource("character_table.json")!!.readText()
        val type = object : TypeToken<Map<String, Operator>>() {}.type
        Gson().fromJson<Map<String, Operator>>(json, type).values.toList()
    }

    private val recruitOperators: List<RecruitOperator> = operators
        .filter(Operator::isRecuritable)
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
            .maxBy { it.value }
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

