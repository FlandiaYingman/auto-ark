package top.anagke.auto_ark.ark.operate

import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.match
import top.anagke.auto_ark.adb.sleep
import top.anagke.auto_ark.ark.AutoArk.Companion.arkToday
import top.anagke.auto_ark.ark.jumpOut
import top.anagke.auto_ark.util.minutes
import java.time.DayOfWeek.*

class OperateLevel
private constructor(
    val name: String,
    val description: String = "",
    val timeout: Long = 5.minutes,
    val entry: Device.() -> Boolean,
) {

    companion object {

        val levelsMap get() = mutableLevels.toMap()

        val levels get() = mutableLevels.values.toList()

        private val mutableLevels = mutableMapOf<String, OperateLevel>()

        fun operateLevel(
            name: String,
            description: String = "",
            timeout: Long = 5.minutes,
            entry: Device.() -> Boolean,
        ): OperateLevel {
            val operateLevel = OperateLevel(name, description, timeout, entry)
            mutableLevels[name] = operateLevel
            return operateLevel
        }


        //作战记录
        val LS_5 = operateLevel("LS-5", "作战记录") {
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //资源收集
            tap(643, 363).sleep() //战术演习
            tap(945, 177).sleep() //LS-5
            return@operateLevel true
        }

        //龙门币
        val CE_5 = operateLevel("CE-5", "龙门币") {
            if (arkToday !in listOf(TUESDAY, THURSDAY, SATURDAY, SUNDAY)) return@operateLevel false
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //资源收集
            tap(438, 349).sleep()
            tap(945, 177).sleep() //CE-5
            return@operateLevel true
        }

        //技巧概要
        val CA_5 = operateLevel("CA-5", "技巧概要") {
            if (arkToday !in listOf(TUESDAY, WEDNESDAY, FRIDAY, SUNDAY)) return@operateLevel false
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //资源收集
            tap(229, 357).sleep()
            tap(945, 177).sleep() //CA-5
            return@operateLevel true
        }

        //重装/医疗芯片
        val PR_A_2 = operateLevel("PR-A-2", "重装/医疗芯片") {
            if (arkToday !in listOf(MONDAY, THURSDAY, FRIDAY, SUNDAY)) return@operateLevel false
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            tap(842, 329).sleep()
            tap(830, 258).sleep() //PR-X-2
            return@operateLevel true
        }

        //狙击/术士芯片
        val PR_B_2 = operateLevel("PR-B-2", "狙击/术士芯片") {
            if (arkToday !in listOf(WEDNESDAY, THURSDAY, SATURDAY, SUNDAY)) return@operateLevel false
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            tap(1269, 350).sleep()
            tap(830, 258).sleep() //PR-X-2
            return@operateLevel true
        }

        //先锋/辅助芯片
        val PR_C_2 = operateLevel("PR-C-2", "先锋/辅助芯片") {
            if (arkToday !in listOf(MONDAY, TUESDAY, FRIDAY, SATURDAY)) return@operateLevel false
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            tap(1060, 353).sleep()
            tap(830, 258).sleep() //PR-X-2
            return@operateLevel true
        }

        //近卫/特种芯片
        val PR_D_2 = operateLevel("PR-D-2", "近卫/特种芯片") {
            if (arkToday !in listOf(TUESDAY, WEDNESDAY, SATURDAY, SUNDAY)) return@operateLevel false
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            swipe(640, 360, 640, 640, duration = 1000).sleep()
            tap(1114, 355).sleep()
            tap(830, 258).sleep() //PR-X-2
            return@operateLevel true
        }

        //当期剿灭作战
        val annihilation = operateLevel("剿灭作战", "当期", timeout = 30L * 60L * 1000L) {
            tap(970, 203).sleep() //终端
            if (match(hasAnnihilation)) {
                tap(1000, 665).sleep()
                tap(835, 400).sleep()
                return@operateLevel true
            } else {
                jumpOut()
                return@operateLevel false
            }
        }


        // MN-8（玛莉娅·临光）
        val MN_8 = operateLevel("MN-8", description = "玛莉亚·临光") {
            tap(970, 203).sleep() //终端
            tap(404, 566).sleep().sleep() //进入活动，等待过场动画
            tap(959, 435).sleep() //进入“大竞技场”
            swipe(1220, 375, 60, 375, 200).sleep() //划到最末端
            tap(130, 299).sleep() //MN-8
            val entered = match(atPrepareScreen, atPrepareScreen_autoDeployDisabled)
            if (entered.not()) {
                jumpOut()
            }
            return@operateLevel entered
        }

        // MN-8（玛莉娅·临光）
        val MN_6 = operateLevel("MN-6", description = "玛莉亚·临光") {
            tap(970, 203).sleep() //终端
            tap(404, 566).sleep().sleep() //进入活动，等待过场动画
            tap(959, 435).sleep() //进入“大竞技场”
            swipe(1220, 375, 60, 375, 200).sleep() //划到最末端
            nswipe(640, 360, 640 + 1000, 360, 5000, 5000).sleep() //划到MN-6
            tap(276, 295).sleep() //MN-6
            val entered = match(atPrepareScreen, atPrepareScreen_autoDeployDisabled)
            if (entered.not()) {
                jumpOut()
            }
            return@operateLevel entered
        }


        val NL_8 = operateLevel("NL-8", description = "糖组，长夜临光") {
            tap(970, 203).sleep() //终端
            tap(404, 566).sleep().sleep() //进入活动，等待过场动画
            tap(1012, 446).sleep() //进入“大骑士领”
            swipe(1220, 375, 60, 375, 200).sleep() //划到最末端
            tap(242, 350).sleep() //NL-8
            val entered = match(atPrepareScreen, atPrepareScreen_autoDeployDisabled)
            if (entered.not()) {
                jumpOut()
            }
            return@operateLevel entered
        }

        val NL_9 = operateLevel("NL-9", description = "晶体元件，长夜临光") {
            tap(970, 203).sleep() //终端
            tap(404, 566).sleep().sleep() //进入活动，等待过场动画
            tap(1012, 446).sleep() //进入“大骑士领”
            swipe(1220, 375, 60, 375, 200).sleep() //划到最末端
            tap(439, 241).sleep() //NL-9
            val entered = match(atPrepareScreen, atPrepareScreen_autoDeployDisabled)
            if (entered.not()) {
                jumpOut()
            }
            return@operateLevel entered
        }

        val NL_10 = operateLevel("NL-10", description = "扭转醇，长夜临光") {
            tap(970, 203).sleep() //终端
            tap(404, 566).sleep().sleep() //进入活动，等待过场动画
            tap(1012, 446).sleep() //进入“大骑士领”
            swipe(1220, 375, 60, 375, 200).sleep() //划到最末端
            tap(515, 498).sleep() //NL-8
            val entered = match(atPrepareScreen, atPrepareScreen_autoDeployDisabled)
            if (entered.not()) {
                jumpOut()
            }
            return@operateLevel entered
        }

    }

    override fun toString(): String {
        val name = this.name
        val description = if (this.description.isNotEmpty()) "（$description）" else ""
        return "$name$description"
    }

}
