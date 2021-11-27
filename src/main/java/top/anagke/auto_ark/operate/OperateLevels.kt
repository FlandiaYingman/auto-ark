package top.anagke.auto_ark.operate

import top.anagke.auto_android.util.minutes
import top.anagke.auto_ark.AutoArk.Companion.arkToday
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.match
import top.anagke.auto_ark.adb.notMatch
import top.anagke.auto_ark.adb.sleep
import top.anagke.auto_ark.jumpOut
import top.anagke.auto_ark.operate.LevelEntryState.*
import top.anagke.auto_ark.operate.OperateLevel.Companion.PR_A_1
import java.time.DayOfWeek.*

typealias LevelEntry = Device.(LevelEntryController) -> Unit

enum class LevelEntryState {
    SUCCESSFUL,
    UNOPENED,
    FAILED
}

class LevelEntryController(
    var state: LevelEntryState = SUCCESSFUL,
)

class OperateLevel
private constructor(
    val name: String,
    val description: String = "",
    val timeout: Long = 5.minutes,
    val entry: LevelEntry,
) {

    companion object {

        val levelsMap get() = mutableLevels.toMap()

        val levels get() = mutableLevels.values.toList()

        private val mutableLevels = mutableMapOf<String, OperateLevel>()

        fun operateLevel(
            name: String,
            description: String = "",
            timeout: Long = 5.minutes,
            entry: LevelEntry,
        ): OperateLevel {
            val operateLevel = OperateLevel(name, description, timeout, entry)
            mutableLevels[name] = operateLevel
            return operateLevel
        }


        val LS_5 = operateLevel("LS-5", "作战记录") {
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //资源收集
            tap(643, 363).sleep() //战术演习
            tap(945, 177).sleep() //LS-5
        }
        val CE_5 = operateLevel("CE-5", "龙门币") {
            if (arkToday !in listOf(TUESDAY, THURSDAY, SATURDAY, SUNDAY)) {
                it.state = UNOPENED
                return@operateLevel
            }
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //资源收集
            tap(438, 349).sleep()
            tap(945, 177).sleep() //CE-5
        }
        val CA_5 = operateLevel("CA-5", "技巧概要") {
            if (arkToday !in listOf(TUESDAY, WEDNESDAY, FRIDAY, SUNDAY)) {
                it.state = UNOPENED
                return@operateLevel
            }
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //资源收集
            tap(229, 357).sleep()
            tap(945, 177).sleep() //CA-5
        }

        val PR_A_1 = operateLevel("PR-A-1", "重装/医疗芯片") {
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            tap(1062, 368, 840, 363, 1272, 365).sleep() //摧枯拉朽
            tap(403, 438).sleep() //PR-X-1
        }
        val PR_B_1 = operateLevel("PR-B-1", "狙击/术士芯片") {
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            tap(848, 364).sleep() //势不可挡
            tap(403, 438).sleep() //PR-X-1
        }
        val PR_C_1 = operateLevel("PR-C-1", "先锋/辅助芯片") {
            if (arkToday !in listOf(MONDAY, TUESDAY, FRIDAY, SATURDAY)) {
                it.state = UNOPENED
                return@operateLevel
            }
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            tap(1062, 364).sleep() //身先士卒
            tap(403, 438).sleep() //PR-X-1
        }
        val PR_D_1 = operateLevel("PR-D-1", "近卫/特种芯片") {
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            tap(1272, 365).sleep() //固若金汤
            tap(403, 438).sleep() //PR-X-1
        }

        val PR_A_2 = operateLevel("PR-A-2", "重装/医疗芯片组") {
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            tap(1062, 368, 840, 363, 1272, 365).sleep() //摧枯拉朽
            tap(830, 258).sleep() //PR-X-2
        }
        val PR_B_2 = operateLevel("PR-B-2", "狙击/术士芯片组") {
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            tap(848, 364).sleep() //势不可挡
            tap(830, 258).sleep() //PR-X-2
        }
        val PR_C_2 = operateLevel("PR-C-2", "先锋/辅助芯片组") {
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            tap(1062, 364).sleep() //身先士卒
            tap(830, 258).sleep() //PR-X-2
        }
        val PR_D_2 = operateLevel("PR-D-2", "近卫/特种芯片组") {
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            tap(1272, 365).sleep() //固若金汤
            tap(830, 258).sleep() //PR-X-2
        }

        val annihilation = operateLevel("剿灭作战", "当期", timeout = 30L * 60L * 1000L) {
            tap(970, 203).sleep() //终端
            if (match(hasAnnihilation)) {
                tap(1000, 665).sleep()
                tap(835, 400).sleep()
            }
        }

        val MN_8 = operateLevel("MN-8", description = "玛莉亚·临光") {
            tap(970, 203).sleep() //终端
            tap(404, 566).sleep().sleep() //进入活动，等待过场动画
            tap(959, 435).sleep() //进入“大竞技场”
            swipe(1220, 375, 60, 375, 200).sleep() //划到最末端
            tap(130, 299).sleep() //MN-8
        }
        val MN_6 = operateLevel("MN-6", description = "玛莉亚·临光") {
            tap(970, 203).sleep() //终端
            tap(404, 566).sleep().sleep() //进入活动，等待过场动画
            tap(959, 435).sleep() //进入“大竞技场”
            swipe(1220, 375, 60, 375, 200).sleep() //划到最末端
            drag(640, 360, 640 + 1000, 360, 1.0).sleep() //划到MN-6
            tap(276, 295).sleep() //MN-6
        }

        val NL_8 = operateLevel("NL-8", description = "糖组，长夜临光") {
            tap(970, 203).sleep() //终端
            tap(404, 566).sleep().sleep() //进入活动，等待过场动画
            tap(1012, 446).sleep() //进入“大骑士领”
            swipe(1220, 375, 60, 375, 200).sleep() //划到最末端
            tap(242, 350).sleep() //NL-8
        }
        val NL_9 = operateLevel("NL-9", description = "晶体元件，长夜临光") {
            tap(970, 203).sleep() //终端
            tap(404, 566).sleep().sleep() //进入活动，等待过场动画
            tap(1012, 446).sleep() //进入“大骑士领”
            swipe(1220, 375, 60, 375, 200).sleep() //划到最末端
            tap(439, 241).sleep() //NL-9
        }
        val NL_10 = operateLevel("NL-10", description = "扭转醇，长夜临光") {
            tap(970, 203).sleep() //终端
            tap(404, 566).sleep().sleep() //进入活动，等待过场动画
            tap(1012, 446).sleep() //进入“大骑士领”
            swipe(1220, 375, 60, 375, 200).sleep() //划到最末端
            tap(515, 498).sleep() //NL-8
        }

    }

    override fun toString(): String {
        val name = this.name
        val description = if (this.description.isNotEmpty()) "（$description）" else ""
        return "$name$description"
    }

}

fun OperateLevel.enter(device: Device): LevelEntryState {
    val state = LevelEntryController().also { this.entry(device, it) }.state
    if (device.notMatch(atPrepareScreen, atPrepareScreen_autoDeployDisabled)) {
        device.jumpOut()
        return FAILED
    }
    return state
}

fun main() {
    PR_A_1.enter(Device())
}
