package top.anagke.auto_ark.riic

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.encodeToString
import top.anagke.auto_android.img.Pos

data class ArkRIICConf(
    val 高级模式: Boolean = false,
    val 宿舍保留: List<Int> = listOf(2, 2, 2, 0),
    val 无人机房间: Pos = Pos(20, 410),
    val 无人机房间类型: String = "MANUFACTURE",
    val 计划: List<Plan> = listOf(
        Plan(
            listOf("B101", "B102"),
            listOf(
                // 孑组
                Sch(Op("孑"), Op("德克萨斯"), Op("拉普兰德"), room = "TRADING"),
                // 通用组
                Sch(Op("能天使"), Op("海蒂"), Op("空弦"), room = "TRADING"),
                // 通用组
                Sch(Op("月见夜"), Op("空爆"), Op("古米"), room = "TRADING"),
            ),
        ),
        Plan(
            listOf("B201", "B202"),
            listOf(
                // 温蒂森蚺组
                Sch(Op("温蒂"), Op("森蚺"), Op("清流"), room = "MANUFACTURE"),
                // 赤金组
                Sch(Op("砾"), Op("夜烟"), Op("斑点"), room = "MANUFACTURE"),
                // 通用组
                Sch(Op("远牙"), Op("赫默"), Op("白面鸮"), room = "MANUFACTURE"),
            ),
        ),
        Plan(
            listOf("B301", "B302"),
            listOf(
                // 水月组
                Sch(Op("水月"), Op("香草"), Op("史都华德"), room = "MANUFACTURE"),
                // 红云组（稀音只能经验）
                Sch(Op("红云"), Op("黑角"), Op("刻俄柏"), room = "MANUFACTURE"),
                // 经验组
                Sch(Op("红豆"), Op("白雪"), Op("霜叶"), room = "MANUFACTURE"),
            ),
        ),
    )
) {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println(Yaml.default.encodeToString(ArkRIICConf()))
        }
    }
}