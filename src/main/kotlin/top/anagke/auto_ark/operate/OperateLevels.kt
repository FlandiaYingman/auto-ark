package top.anagke.auto_ark.operate

import top.anagke.auto_android.img.Img
import top.anagke.auto_android.img.ocr
import top.anagke.auto_android.util.Pos
import top.anagke.auto_android.util.Rect
import top.anagke.auto_android.util.Size
import top.anagke.auto_android.util.minutes
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.match
import top.anagke.auto_ark.adb.notMatch
import top.anagke.auto_ark.adb.sleep
import top.anagke.auto_ark.jumpOut
import top.anagke.auto_ark.operate.LevelEntryState.FAILED
import top.anagke.auto_ark.operate.LevelEntryState.SUCCESSFUL

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

    @Suppress("unused")
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
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //资源收集
            tap(438, 349).sleep()
            tap(945, 177).sleep() //CE-5
        }
        val CA_5 = operateLevel("CA-5", "技巧概要") {
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //资源收集
            tap(229, 357).sleep()
            tap(945, 177).sleep() //CA-5
        }

        val PR_A_1 = operateLevel("PR-A-1", "重装/医疗芯片") {
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            swipe(920, 360, 360, 360).sleep()
            val pos = findChipLevel(cap(), "固若金汤")
            tap(pos.x, pos.y).sleep() //固若金汤
            tap(403, 438).sleep() //PR-X-1
        }
        val PR_B_1 = operateLevel("PR-B-1", "狙击/术士芯片") {
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            swipe(920, 360, 360, 360).sleep()
            val pos = findChipLevel(cap(), "摧枯拉朽")
            tap(pos.x, pos.y).sleep() //摧枯拉朽
            tap(403, 438).sleep() //PR-X-1
        }
        val PR_C_1 = operateLevel("PR-C-1", "先锋/辅助芯片") {
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            swipe(920, 360, 360, 360).sleep()
            val pos = findChipLevel(cap(), "势不可挡")
            tap(pos.x, pos.y).sleep() //势不可挡
            tap(403, 438).sleep() //PR-X-1
        }
        val PR_D_1 = operateLevel("PR-D-1", "近卫/特种芯片") {
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            swipe(920, 360, 360, 360).sleep()
            val pos = findChipLevel(cap(), "身先士卒")
            tap(pos.x, pos.y).sleep() //身先士卒
            tap(403, 438).sleep() //PR-X-1
        }

        val PR_A_2 = operateLevel("PR-A-2", "重装/医疗芯片组") {
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            swipe(920, 360, 360, 360).sleep()
            val pos = findChipLevel(cap(), "固若金汤")
            tap(pos.x, pos.y).sleep() //固若金汤
            tap(830, 258).sleep() //PR-X-2
        }
        val PR_B_2 = operateLevel("PR-B-2", "狙击/术士芯片组") {
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            swipe(920, 360, 360, 360).sleep()
            val pos = findChipLevel(cap(), "摧枯拉朽")
            tap(pos.x, pos.y).sleep() //摧枯拉朽
            tap(830, 258).sleep() //PR-X-2
        }
        val PR_C_2 = operateLevel("PR-C-2", "先锋/辅助芯片组") {
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            swipe(920, 360, 360, 360).sleep()
            val pos = findChipLevel(cap(), "势不可挡")
            tap(pos.x, pos.y).sleep() //势不可挡
            tap(830, 258).sleep() //PR-X-2
        }
        val PR_D_2 = operateLevel("PR-D-2", "近卫/特种芯片组") {
            tap(970, 203).sleep() //终端
            tap(822, 670).sleep() //Resource Collection
            swipe(920, 360, 360, 360).sleep()
            val pos = findChipLevel(cap(), "身先士卒")
            tap(pos.x, pos.y).sleep() //身先士卒
            tap(830, 258).sleep() //PR-X-2
        }

        private fun findChipLevel(cap: Img, name: String): Pos {
            return listOf(
                Rect(Pos(397 + 208 * 0, 463), Size(190, 31)),
                Rect(Pos(397 + 208 * 1, 463), Size(190, 31)),
                Rect(Pos(397 + 208 * 2, 463), Size(190, 31)),
                Rect(Pos(397 + 208 * 3, 463), Size(190, 31))
            )
                .find { cap.crop(it).invert().ocr(listOf("固若金汤", "势不可挡", "身先士卒", "摧枯拉朽")) == name }
                ?.center()
                ?: Pos(-1, -1)
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

        val MB_8 = operateLevel("MB-8", description = "孤岛风云，异铁组") {
            tap(1176, 141).sleep().sleep() //进入活动，等待过场动画
            tap(1140, 157).sleep() //进入“越狱计划”
            tap(1139, 521).sleep() //进入“MB-8”
        }
        val MB_7 = operateLevel("MB-7", description = "孤岛风云，酮凝集组") {
            tap(1176, 141).sleep().sleep() //进入活动，等待过场动画
            tap(1140, 157).sleep() //进入“越狱计划”
            tap(840, 521).sleep() //进入“MB-7”
        }
        val MB_6 = operateLevel("MB-6", description = "孤岛风云，酮凝集组") {
            tap(1176, 141).sleep().sleep() //进入活动，等待过场动画
            tap(1140, 157).sleep() //进入“越狱计划”
            tap(840, 392).sleep() //进入“MB-6”
        }


        val BI_8 = operateLevel("BI-8", description = "风雪过境，研磨石") {
            tap(1176, 141).sleep().sleep() //进入活动，等待过场动画
            tap(1164, 334).sleep() //进入“雪山大典”
            drag(1280, 360, -1280 * 3, 360).sleep() //划到最末端
            tap(303, 388).sleep() //进入“BI-8”
        }
        val BI_7 = operateLevel("BI-7", description = "风雪过境，聚酸酯组") {
            tap(1176, 141).sleep().sleep() //进入活动，等待过场动画
            tap(1164, 334).sleep() //进入“雪山大典”
            drag(1280, 360, -1280 * 3, 360).sleep() //划到最末端
            tap(142, 291).sleep() //进入“BI-7”
        }
        val BI_6 = operateLevel("BI-6", description = "风雪过境，炽合金") {
            tap(1176, 141).sleep().sleep() //进入活动，等待过场动画
            tap(1164, 334).sleep() //进入“雪山大典”
            drag(1280, 360, -1280 * 3, 360).sleep() //划到最末端
            drag(389, 373, 535, 390).sleep() //往回划一些
            tap(61, 204).sleep() //进入“BI-6”
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
