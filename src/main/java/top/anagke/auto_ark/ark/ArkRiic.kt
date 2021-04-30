package top.anagke.auto_ark.ark

import kotlinx.serialization.Serializable
import top.anagke.auto_ark.adb.OpsContext
import top.anagke.auto_ark.adb.assert
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.back
import top.anagke.auto_ark.adb.ops
import top.anagke.auto_ark.adb.tap

@Serializable
data class RiicProps()

private val riicProps = arkProps.riicProps

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
    B404(5, 904, 522),
}

fun autoRiic() = ops {
    assert(atMainScreen)

    tap(986, 624) //进入基建
    //await(atRIICScreen)

    riicAssign()
    //await(atRIICScreen)

    riicCollect()
    //await(atRIICScreen)

    back()
    await(atMainScreen)
}

private fun OpsContext.riicAssign() {
    RiicFacility.values().filter { it != RiicFacility.CONTROL_CENTER }.forEach { facility ->
        tap(facility.riicScreenX, facility.riicScreenY) //进入房间
        tap(640, 360, delay = 0) //确保“进驻信息”为非开启状态
        tap(69, 297) //进驻信息
        tap(919, 159) //第一进驻栏

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
        repeat(facility.operatorLimit * 2) {
            tap(operatorPos[it].first, operatorPos[it].second, delay = 0)
        }

        tap(1182, 677, delay = 1500) //确认
        back(delay = 1000) //退出房间
    }
}

private fun OpsContext.riicCollect() {
    tap(1203, 92) //打开基建提醒
    repeat(3) { tap(239, 693, delay = 1500) } //收取贸易站产物、制造站产物和干员信赖
    tap(1136, 94) //关闭基建提醒
}