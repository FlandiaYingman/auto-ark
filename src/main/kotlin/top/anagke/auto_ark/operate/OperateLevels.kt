package top.anagke.auto_ark.operate

import top.anagke.auto_android.*
import top.anagke.auto_android.img.ocrWord
import top.anagke.auto_android.util.Pos
import top.anagke.auto_android.util.Rect
import top.anagke.auto_android.util.Size
import top.anagke.auto_android.util.minutes
import top.anagke.auto_ark.jumpOut
import top.anagke.auto_ark.operate.LevelEntryState.FAILED
import top.anagke.auto_ark.operate.LevelEntryState.SUCCESSFUL
import top.anagke.auto_ark.operate.OperateLevel.Companion.剿灭作战
import top.anagke.auto_ark.operate.OperateTemplates.关卡信息界面_代理指挥关闭
import top.anagke.auto_ark.operate.OperateTemplates.关卡信息界面_代理指挥开启
import top.anagke.auto_ark.operate.OperateTemplates.剿灭_已刷满
import top.anagke.auto_ark.operate.OperateTemplates.剿灭_已通过
import top.anagke.auto_ark.operate.Poses.PR_X_1
import top.anagke.auto_ark.operate.Poses.PR_X_2
import top.anagke.auto_ark.operate.Poses.XX_5
import top.anagke.auto_ark.operate.Poses.终端
import top.anagke.auto_ark.operate.Poses.终端_主题曲
import top.anagke.auto_ark.operate.Poses.终端_每周部署
import top.anagke.auto_ark.operate.Poses.终端_资源收集
import top.anagke.auto_ark.operate.资源收集芯片.*

typealias LevelEntry = Device.(LevelEntryController) -> Unit

enum class LevelEntryState {
    SUCCESSFUL, UNOPENED, FAILED
}

class LevelEntryController(
    var state: LevelEntryState = SUCCESSFUL,
)

private enum class 资源收集芯片 {
    固若金汤, 摧枯拉朽, 势不可挡, 身先士卒;

    fun where(dev: Device): Pos? {
        val cap = dev.cap()
        val chipTextRegion = listOf(
            Rect(Pos(397 + 208 * 0, 463), Size(190, 31)),
            Rect(Pos(397 + 208 * 1, 463), Size(190, 31)),
            Rect(Pos(397 + 208 * 2, 463), Size(190, 31)),
            Rect(Pos(397 + 208 * 3, 463), Size(190, 31))
        )
        return chipTextRegion.find {
            val image = cap.crop(it)
            val words = values().map(资源收集芯片::name)
            ocrWord(image, words = words) == name
        }?.center()
    }
}

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


        val LS_5 = operateLevel("LS-5", "战术演习（作战记录）") {
            tap(终端).sleep()
            tap(终端_资源收集).nap()
            tap(643, 363).sleep() //战术演习
            tap(XX_5).nap() //LS-5
        }
        val CE_5 = operateLevel("CE-5", "货物运送（龙门币）") {
            tap(终端).sleep()
            tap(终端_资源收集).nap()
            tap(438, 349).sleep() //资源保障
            tap(XX_5).nap() //CE-5
        }
        val CA_5 = operateLevel("CA-5", "空中威胁（技巧概要）") {
            tap(终端).sleep()
            tap(终端_资源收集).nap()
            tap(229, 357).sleep() //空中威胁
            tap(XX_5).nap() //CA-5
        }

        val PR_A_1 = operateLevel("PR-A-1", "固若金汤（重装/医疗芯片）") {
            tap(终端).sleep()
            tap(终端_资源收集).nap()
            dragd(640, 360, -1280, 0)

            tap(固若金汤.where(this) ?: return@operateLevel).sleep()
            tap(PR_X_1).nap()
        }
        val PR_B_1 = operateLevel("PR-B-1", "摧枯拉朽（狙击/术士芯片）") {
            tap(终端).sleep()
            tap(终端_资源收集).nap()
            dragd(640, 360, -1280, 0)

            tap(摧枯拉朽.where(this) ?: return@operateLevel).sleep()
            tap(PR_X_1).nap()
        }
        val PR_C_1 = operateLevel("PR-C-1", "势不可挡（先锋/辅助芯片）") {
            tap(终端).sleep()
            tap(终端_资源收集).nap()
            dragd(640, 360, -1280, 0)

            tap(势不可挡.where(this) ?: return@operateLevel).sleep()
            tap(PR_X_1).nap()
        }
        val PR_D_1 = operateLevel("PR-D-1", "身先士卒（近卫/特种芯片）") {
            tap(终端).sleep()
            tap(终端_资源收集).nap()
            dragd(640, 360, -1280, 0)

            tap(身先士卒.where(this) ?: return@operateLevel).sleep()
            tap(PR_X_1).nap()
        }

        val PR_A_2 = operateLevel("PR-A-2", "固若金汤（重装/医疗芯片组）") {
            tap(终端).sleep()
            tap(终端_资源收集).nap()
            dragd(640, 360, -1280, 0)

            tap(固若金汤.where(this) ?: return@operateLevel).sleep()
            tap(PR_X_2).nap()
        }
        val PR_B_2 = operateLevel("PR-B-2", "摧枯拉朽（狙击/术士芯片组）") {
            tap(终端).sleep()
            tap(终端_资源收集).nap()
            dragd(640, 360, -1280, 0)

            tap(摧枯拉朽.where(this) ?: return@operateLevel).sleep()
            tap(PR_X_2).nap()
        }
        val PR_C_2 = operateLevel("PR-C-2", "势不可挡（先锋/辅助芯片组）") {
            tap(终端).sleep()
            tap(终端_资源收集).nap()
            dragd(640, 360, -1280, 0)

            tap(势不可挡.where(this) ?: return@operateLevel).sleep()
            tap(PR_X_2).nap()
        }
        val PR_D_2 = operateLevel("PR-D-2", "身先士卒（近卫/特种芯片组）") {
            tap(终端).sleep()
            tap(终端_资源收集).nap()
            dragd(640, 360, -1280, 0)

            tap(身先士卒.where(this) ?: return@operateLevel).sleep()
            tap(PR_X_2).nap()
        }


        val 剿灭作战 = operateLevel("剿灭作战", "当期委托（合成玉）", timeout = 15.minutes) {
            tap(终端).sleep()
            tap(终端_每周部署).nap()
            if (notMatch(剿灭_已刷满)) {
                tap(838, 385).sleep() //当期委托
                if (match(剿灭_已通过)) {
                    //已通过当期委托
                } else {
                    //尚未通过当期委托
                    back().nap() //跳出关卡信息界面
                    tap(1150, 680).nap() //切换剿灭作战
                    tap(1184, 263).sleep() //选择龙门外环
                    //选择龙门外环的原因是：如果一个账号尚未通过当期委托，那么很大概率是因为练度不够。
                    //如果是因为练度不够，那么这个账号很可能也尚未通过龙门市区，所以保险起见，选择龙门外环。
                }
            }
        }

        val MAIN_1_7 = operateLevel("1-7", description = "固源岩") {
            tap(终端).sleep() //进入“终端”
            tap(终端_主题曲).nap() //进入“主题曲”
            tap(66, 132).nap() //进入“幻灭”
            tap(66, 132).nap() //进入“觉醒”

            dragd(950, 360, -950, 0) //确保处于“二次呼吸”
            tap(1200, 360).nap() //取消拖动

            tap(330, 375).sleep() //进入“黑暗时代（下）”

            dragd(160, 360, 4480, 0) //确保处于最左边
            dragd(950, 360, -1750, 0) //确保处于“1-7”
            tap(1092, 222).nap() //进入“1-7”
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
    if (device.notMatch(关卡信息界面_代理指挥开启, 关卡信息界面_代理指挥关闭)) {
        device.jumpOut()
        return FAILED
    }
    return state
}

fun main() {
    剿灭作战.enter(Device())
}