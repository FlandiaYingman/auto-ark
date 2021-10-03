package top.anagke.auto_ark.ark.riic

import kotlinx.serialization.Serializable
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.nap
import top.anagke.auto_ark.adb.sleep
import top.anagke.auto_ark.adb.whileNotMatch
import top.anagke.auto_ark.ark.AutoArk
import top.anagke.auto_ark.ark.appConfig
import top.anagke.auto_ark.ark.jumpOut
import top.anagke.auto_ark.ark.template

@Serializable
data class RiicConfig(
    val autoAssigning: Boolean = true,
    val autoAssignControlCenter: Boolean = false,
)


enum class RiicFacility(
    val operatorLimit: Int,
    val riicScreenX: Int,
    val riicScreenY: Int,
) {
    CONTROL_CENTER(5, 841, 154),
    RECEPTION_ROOM(2, 1181, 204),
    HUMAN_RESOURCES_OFFICE(1, 1255, 417),
    B101(3, 54, 305),
    B102(3, 275, 305),
    B103(3, 487, 305),
    B104(5, 821, 305),
    B201(3, 8, 418),
    B202(3, 165, 418),
    B203(3, 383, 418),
    B204(5, 821, 418),
    B301(3, 54, 522),
    B302(3, 275, 522),
    B303(3, 487, 522),
    B304(5, 821, 522),
    B401(5, 904, 626);

    fun isDorm() = when (this) {
        B104, B204, B304, B401 -> true
        else -> false
    }
}

private val atRiicScreen = template("riic/atRiicScreen.png")
val atRiicFacility = template("riic/atRiicFacility.png")
val atRiicDorm = template("riic/atRiicDorm.png")
val isClueEmpty = template("riic/isClueEmpty.png")

class ArkRiic(
    private val device: Device,
) {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            AutoArk(appConfig).autoRiic()
        }
    }

    fun auto() = device.apply {
        //   assert(atMainScreen)

        //     collect()
        assign()
    }

    private fun Device.enterRiic() {
        tap(985, 625) //进入基建
        await(atRiicScreen)
        sleep()
    }

    private fun collect() = device.apply {
        enterRiic()

        tap(1203, 132).nap() //打开基建提醒（下方位置）
        repeat(3) { tap(239, 693).nap() } //收取贸易站产物、制造站产物和干员信赖
        tap(1136, 94).nap() //关闭基建提醒

        tap(1203, 92).nap() //打开基建提醒（一般位置）
        repeat(3) { tap(239, 693).nap() } //收取贸易站产物、制造站产物和干员信赖
        tap(1136, 94).nap() //关闭基建提醒

        tap(1047, 234).sleep() //打开会客室
        tap(414, 617).sleep() //进入详情
        tap(1197, 388).sleep() //进入传递线索
        whileNotMatch(isClueEmpty) {
            tap(74, 183).nap() //选择第一个线索
            tap(1194, 135).sleep() //传递给第一个好友
        }
        back().sleep() // 返回会客室
        tap(1196, 180).sleep() //进入收集线索
        tap(805, 574).sleep() //领取会客室线索
        back()

        jumpOut()
    }

    private fun assign() = device.apply {
        tap(74, 118).sleep() //进驻总览

        //前两个宿舍
        repeat(2) {
            nswipe(1200, 415, 1200, 415 - 880, 2000, 500) //一个宿舍距离
            tap(670, 200).sleep() //第一个房间
            shift(5)
        }

        //后两个宿舍
        nswipe(1200, 415, 1200, 415 - 880, 2000, 500) //一个宿舍距离
        tap(670, 240).sleep() //第三个宿舍
        shift(5)
        tap(670, 600).sleep() //第四个宿舍
        shift(5)

        swipe(1200, 415, 1200, 415 + 2048, 1000).sleep()

        nswipe(1200, 415, 1200, 415 - 190, 1000, 500) //一个房间距离
        tap(670, 200).nap() //第一个房间
        shift(2)

        repeat(3) {
            nswipe(1200, 415, 1200, 415 - 245, 1000, 500) //一层距离
            repeat(3) {
                tap(670, 200).nap() //第一个房间
                shift(3) //贸易站、制造站或发电站
                nswipe(1200, 415, 1200, 415 - 190, 1000, 500) //一个房间距离
            }
            if (it < 3 - 1) {
                nswipe(1200, 415, 1200, 415 - 190, 1000, 500) //一个房间距离
                tap(670, 200).nap() //第一个房间
                shift(1) //辅助设施
            }
        }
    }


    private fun shift(limit: Int) = device.apply {
        val operatorPos = listOf(
            490 to 253,
            478 to 446,
            597 to 249,
            607 to 464,
            732 to 256,
            775 to 501,
            931 to 266,
            913 to 445,
            1042 to 235,
            1033 to 454,
        )
        repeat(limit * 2) {
            tap(operatorPos[it].first, operatorPos[it].second)
        }

        tap(1180, 675) //确认
        tap(1180, 675).nap() //确认
    }

}