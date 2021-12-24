package top.anagke.auto_ark.riic

import kotlinx.serialization.Serializable
import top.anagke.auto_android.AutoModule
import top.anagke.auto_android.Device
import top.anagke.auto_android.assert
import top.anagke.auto_android.await
import top.anagke.auto_android.nap
import top.anagke.auto_android.sleep
import top.anagke.auto_android.whileNotMatch
import top.anagke.auto_ark.atMainScreen
import top.anagke.auto_ark.jumpOut
import top.anagke.auto_ark.template

@Serializable
data class RiicConfig(
    val autoAssigning: Boolean = true,
    val autoAssignControlCenter: Boolean = false,
)


private val atRiicScreen = template("riic/atRiicScreen.png")
val isClueEmpty = template("riic/isClueEmpty.png")

class ArkRiic(
    private val device: Device,
) : AutoModule {

    override fun run() = device.run {
        assert(atMainScreen)

        enterRiic()
        collect()
        assign()
        jumpOut()
    }

    private fun Device.enterRiic() {
        tap(985, 625) //进入基建
        await(atRiicScreen)
        sleep()
    }

    private fun collect() = device.apply {
        tap(1203, 132).nap() //打开基建提醒（下方位置）
        repeat(3) { tap(239, 693).nap() } //收取贸易站产物、制造站产物和干员信赖
        tap(1136, 94).nap() //关闭基建提醒

        tap(1203, 92).nap() //打开基建提醒（一般位置）
        repeat(3) { tap(239, 693).nap() } //收取贸易站产物、制造站产物和干员信赖
        tap(1136, 94).nap() //关闭基建提醒

        tap(1047, 234).sleep().sleep() //打开会客室
        tap(414, 617).sleep() //进入详情
        tap(1197, 388).sleep() //进入传递线索
        whileNotMatch(isClueEmpty) {
            tap(74, 183).nap() //选择第一个线索
            tap(1194, 135).sleep() //传递给第一个好友
        }
        back().sleep() // 返回会客室
        tap(1196, 180).sleep() //进入收集线索
        tap(805, 574).sleep() //领取会客室线索
        back().nap()
        back().nap()
        back().sleep()
    }

    private fun assign() = device.apply {
        tap(74, 118).sleep() //进驻总览

        //前两个宿舍
        repeat(2) {
            drag(1200, 415, 1200, 415 - 880) //一个宿舍距离
            tap(670, 200).sleep() //第一个房间
            shift(5)
        }

        //后两个宿舍
        drag(1200, 415, 1200, 415 - 880) //一个宿舍距离
        tap(670, 240).sleep() //第三个宿舍
        shift(5)
        tap(670, 600).sleep() //第四个宿舍
        shift(5)

        swipe(1200, 415, 1200, 415 + 2048, 1000).sleep()

        drag(1200, 415, 1200, 415 - 190) //一个房间距离
        tap(670, 200).nap() //第一个房间
        shift(2)

        repeat(3) {
            drag(1200, 415, 1200, 415 - 245) //一层距离
            repeat(3) {
                tap(670, 200).nap() //第一个房间
                shift(3) //贸易站、制造站或发电站
                drag(1200, 415, 1200, 415 - 190) //一个房间距离
            }
            if (it < 3 - 1) {
                drag(1200, 415, 1200, 415 - 190) //一个房间距离
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

        tap(1180, 675).sleep() //确认
        tap(1180, 675).sleep() //确认
    }

}

