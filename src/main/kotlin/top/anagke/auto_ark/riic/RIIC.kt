package top.anagke.auto_ark.riic

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.opencv.core.*
import org.opencv.core.Core.*
import org.opencv.imgcodecs.Imgcodecs.*
import org.opencv.imgproc.Imgproc.*
import top.anagke.auto_android.device.Device
import top.anagke.auto_android.device.nap
import top.anagke.auto_android.device.whileNotMatch
import top.anagke.auto_android.img.OpenCV
import top.anagke.auto_android.util.Pos
import top.anagke.auto_ark.riic.ArkRiic.Companion.心情增序
import top.anagke.auto_ark.riic.ArkRiic.Companion.未进驻筛选
import java.time.LocalTime
import java.time.LocalTime.now
import kotlin.time.Duration
import kotlin.time.toJavaDuration

private object RIIC {
    init {
        OpenCV.init()
    }

    fun init() {}
}

private val CHARACTER_TABLE_JSON_OBJ = RIIC.javaClass.getResourceAsStream("character_table.json").reader().use {
    Gson().fromJson(it, JsonObject::class.java)
}
private val BUILDING_DATA_JSON_OBJ = RIIC.javaClass.getResourceAsStream("building_data.json").reader().use {
    Gson().fromJson(it, JsonObject::class.java)
}
private val OPERATOR_ID_NAME_MAP = CHARACTER_TABLE_JSON_OBJ.keySet().associateWith {
    CHARACTER_TABLE_JSON_OBJ.getAsJsonObject(it).getAsJsonPrimitive("name").asString
}
private val OPERATOR_NAME_ID_MAP = CHARACTER_TABLE_JSON_OBJ.keySet().associateBy {
    CHARACTER_TABLE_JSON_OBJ.getAsJsonObject(it).getAsJsonPrimitive("name").asString
}


@Serializable
data class Op(
    val name: String,
    val elite: Int = 2,
    val level: Int = 90,
)

@Serializable
data class Sch(
    val ops: List<Op>, val room: String
) {
    @Transient
    val buffs = schBuffs(this)
}

fun Sch(
    vararg ops: Op, room: String
) = Sch(ops.toList(), room)


data class Buff(
    val id: String,
    val name: String,
    val iconName: String,
) {

    companion object {
        private val ICON_BACKGROUND = RIIC.javaClass.getResourceAsStream("skill_icons/_bkg.png").readBytes()
            .let { imdecode(MatOfByte(*it), IMREAD_UNCHANGED) }
    }

    val icon = RIIC.javaClass
        .getResourceAsStream("skill_icons/$iconName.png")
        .readBytes()
        .let { imdecode(MatOfByte(*it), IMREAD_UNCHANGED) }.let { overlay(ICON_BACKGROUND, it) }

    val iconTemplate = splitMask(icon)

}

fun schBuffs(sch: Sch): List<List<Buff>> {
    return sch.ops.map { buffs(it, sch.room) }
}

private fun buffs(op: Op, room: String): List<Buff> {
    val opID = OPERATOR_NAME_ID_MAP[op.name]
    val buffs = BUILDING_DATA_JSON_OBJ.getAsJsonObject("chars").getAsJsonObject(opID).getAsJsonArray("buffChar")
        .mapNotNull { buffs ->
            buffs.asJsonObject.getAsJsonArray("buffData").lastOrNull { buff ->
                buff.asJsonObject.getAsJsonObject("cond").let {
                    val elite = it.getAsJsonPrimitive("phase").asInt <= op.elite
                    val level = it.getAsJsonPrimitive("level").asInt <= op.level
                    elite && level
                }
            }?.let { buff ->
                val buffID = buff.asJsonObject.getAsJsonPrimitive("buffId").asString
                val rawBuffObj = BUILDING_DATA_JSON_OBJ.getAsJsonObject("buffs").getAsJsonObject(buffID)
                if (rawBuffObj.getAsJsonPrimitive("roomType").asString != room) null
                else Buff(
                    buffID,
                    rawBuffObj.getAsJsonPrimitive("buffName").asString,
                    rawBuffObj.getAsJsonPrimitive("skillIcon").asString,
                )
            }
        }
    return buffs
}

private val SKILLS_ROW_1 = Rect(400, 263, 880, 40)
private val SKILLS_ROW_2 = Rect(400, 544, 880, 40)

fun main() {
    val screenshot = imread("C:\\Users\\Flandia\\Desktop\\a.png")
    val recognize = recognize(
        screenshot,
        buffs(Op("香草"), "MANUFACTURE").toSet(),
        listOf(buffs(Op("水月"), "MANUFACTURE").toSet())
    )
    println(recognize)
}

private fun recognize(screenshot: Mat, wantedBuffs: Set<Buff>, possibleBuffs: List<Set<Buff>>): List<Pos> {
    // unwantedBuffs = the union set of all proper subset of wantedBuffs in possibleBuffs
    val unwantedBuffs = possibleBuffs
        .filter { it.containsAll(wantedBuffs) && it != wantedBuffs }
        .flatten()

    val row1 = screenshot.submat(SKILLS_ROW_1)
    val row2 = screenshot.submat(SKILLS_ROW_2)
    val result = (wantedBuffs + unwantedBuffs).flatMap { buff ->
        val (template, mask) = buff.iconTemplate
        val res1 = Mat().also { matchTemplate(row1, template, it, TM_CCORR_NORMED, mask) }
        val res2 = Mat().also { matchTemplate(row2, template, it, TM_CCORR_NORMED, mask) }
        listOf(
            findMaxes(res1).map { Pos((it.maxLoc.x / SKILLS_ROW_1.width * 6).toInt(), 0) },
            findMaxes(res2).map { Pos((it.maxLoc.x / SKILLS_ROW_2.width * 6).toInt(), 1) }
        ).flatten()
            .sortedBy { pt -> pt.x * 2 + pt.y }
            .map { pt -> pt to buff }
    }
        .groupBy { (pos, buff) -> pos }
        .map { (pos, buff) -> pos to buff.map { it.second }.toSet() }

    return result.filter { (pos, buffs) -> buffs == wantedBuffs }.map { (pos, buffs) -> pos }
}

private fun overlay(bg: Mat, fg: Mat): Mat {
    val canvas = Mat(bg.size(), CvType.CV_8UC4)
    for (col in 0 until bg.cols()) {
        for (row in 0 until bg.rows()) {
            val bgColor = bg.get(row, col)
            val fgColor = fg.get(row, col)
            val bgAlpha = bgColor[3] / 255.0
            val fgAlpha = fgColor[3] / 255.0
            val canvasAlpha = (bgAlpha + fgAlpha) - (bgAlpha * fgAlpha)
            canvas.put(
                row,
                col,
                bgAlpha * bgColor[0] * (1 - fgAlpha) + fgAlpha * fgColor[0] / canvasAlpha,
                bgAlpha * bgColor[1] * (1 - fgAlpha) + fgAlpha * fgColor[1] / canvasAlpha,
                bgAlpha * bgColor[2] * (1 - fgAlpha) + fgAlpha * fgColor[2] / canvasAlpha,
                canvasAlpha * 255.0
            )
        }
    }
    return canvas
}

private fun findMaxes(m: Mat, threshold: Double = 0.97, thickness: Int = 5): List<MinMaxLocResult> {
    val list = ArrayList<MinMaxLocResult>()
    while (true) {
        val mml = minMaxLoc(m)
        if (mml.maxVal < threshold) break

        list += mml
        rectangle(m, mml.maxLoc, mml.maxLoc, Scalar(0.0), thickness)
    }
    return list
}

private fun splitMask(mat: Mat): Pair<Mat, Mat> {
    val channels = ArrayList<Mat>().apply { split(mat, this) }
    val color = Mat().apply { merge(channels.take(3), this) }
    val alpha = Mat().apply { threshold(channels[3], this, 254.0, 255.0, THRESH_BINARY) }
    return color to alpha
}

fun Device.doShift(room: String = "") {
    val max = when {
        room == "1F01" -> 5
        room == "1F02" -> 2
        room.endsWith("1") || room.endsWith("2") -> 3
        room.endsWith("3") -> 1
        room.endsWith("4") -> 5
        room.endsWith("5") -> 1
        else -> 5
    }
    for (num in 0 until max * 2) {
        tap(Pos(num / 2, num % 2).toScreenPos())
    }
}

fun Device.doShiftAdv(schedule: Sch, fullSchedules: Collection<Sch>) {
    tap(510, 680, description = "清空选择").nap()

    val selectedOperators = mutableSetOf<Op>()
    val selectedPoses = mutableSetOf<Pos>()
    outer@ for (i in 0 until 3) {
        val screenshot = cap().mat

        schedule.ops.zip(schedule.buffs).forEach { (operator, operatorBuffs) ->
            if (selectedOperators.size >= schedule.ops.size) return@forEach
            if (operator in selectedOperators) return@forEach

            val possibleBuffs = fullSchedules.flatMap { sch -> sch.buffs.map { it.toSet() } }
            val poses = recognize(screenshot, operatorBuffs.toSet(), possibleBuffs)
            for (pos in poses) {
                if (selectedOperators.size >= schedule.ops.size) return@forEach
                if (pos in selectedPoses) continue

                tap(pos.toScreenPos(), description = "选择${operator.name}")
                selectedOperators += operator
                selectedPoses += pos
                break
            }
        }
        if (selectedOperators.size >= schedule.ops.size) break@outer

        dragv(640, 360, -900, 0, speed = 0.15, description = "拖拽到下一页面").nap()
        selectedPoses.clear()
    }
}

fun Device.doShiftDorm(preserve: Int, init: Boolean = false) {
    if (init) {
        tap(1180, 40, description = "").nap()
        whileNotMatch(心情增序) {
            tap(605, 170).nap()
        }
        whileNotMatch(未进驻筛选) {
            tap(430, 360).nap()
        }
        tap(950, 550).nap()
    }
    for (num in preserve until 10 - preserve) {
        tap(Pos(num / 2, num % 2).toScreenPos())
    }
}

private fun Pos.toScreenPos(): Pos {
    return Pos(480 + x * 144, 210 + y * 300)
}

enum class 房间(id: String) {
    贸易站("TRADING"), 制造站("MANUFACTURE"), 发电站("<发电站>"), 宿舍("<宿舍>")
}

@Serializable
data class Plan(
    val rooms: List<String>,
    val schedule: List<Sch>,
) {

    val room1 get() = rooms[0]
    val room2 get() = rooms[1]

    val schA get() = schedule[0]
    val schB get() = schedule[1]
    val schY get() = schedule[2]

    val planBegin get() = LocalTime.parse("06:00")
    val planInterval get() = Duration.parse("6h")

    val shiftTime1st: LocalTime get() = planBegin + (planInterval * 0).toJavaDuration()
    val shiftTime2nd: LocalTime get() = planBegin + (planInterval * 1).toJavaDuration()
    val shiftTime3rd: LocalTime get() = planBegin + (planInterval * 2).toJavaDuration()


    fun shiftable(room: String) = when (room) {
        room1 -> when {
            now().equals(shiftTime1st, planInterval / 2) -> true
            now().equals(shiftTime2nd, planInterval / 2) -> true
            else -> false
        }
        room2 -> when {
            now().equals(shiftTime2nd, planInterval / 2) -> true
            now().equals(shiftTime3rd, planInterval / 2) -> true
            else -> false
        }
        else -> false
    }

    fun shift(room: String, fullPlan: Collection<Plan>, device: Device) = device.apply {
        when (room) {
            room1 -> when {
                now().equals(shiftTime1st, planInterval / 2) -> doShiftAdv(schY, fullPlan.flatMap { it.schedule })
                now().equals(shiftTime2nd, planInterval / 2) -> doShiftAdv(schA, fullPlan.flatMap { it.schedule })
            }
            room2 -> when {
                now().equals(shiftTime2nd, planInterval / 2) -> doShiftAdv(schY, fullPlan.flatMap { it.schedule })
                now().equals(shiftTime3rd, planInterval / 2) -> doShiftAdv(schB, fullPlan.flatMap { it.schedule })
            }
        }
    }

    private fun LocalTime.equals(other: LocalTime, bias: Duration = Duration.parse("1h")): Boolean {
        val lower = other - bias.toJavaDuration()
        val higher = other + bias.toJavaDuration()
        return this.between(lower, higher)
    }

    private fun LocalTime.between(begin: LocalTime, end: LocalTime): Boolean {
        return if (begin.isBefore(end)) {
            this.isAfter(begin) && this.isBefore(end)
        } else {
            this.isAfter(begin) || this.isBefore(end)
        }
    }

}
