package top.anagke.auto_ark.ark.operate

import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.sleep

class OperateLevel(
    val name: String,
    val timeout: Long = 5L * 60L * 1000L,
    val enter: Device.() -> Unit,
)

//作战记录
val LS_5 = OperateLevel("LS-5（作战记录）") {
    tap(970, 203).sleep() //终端
    tap(822, 670).sleep() //资源收集
    tap(643, 363).sleep() //战术演习
    tap(945, 177).sleep() //LS-5
}

//龙门币
val CE_5 = OperateLevel("CE-5（龙门币）") {
    tap(970, 203).sleep() //终端
    tap(822, 670).sleep() //资源收集
    tap(438, 349).sleep()
    tap(945, 177).sleep() //CE-5
}

//技巧概要
val CA_5 = OperateLevel("CA-5（技巧概要）") {
    tap(970, 203).sleep() //终端
    tap(822, 670).sleep() //资源收集
    tap(229, 357).sleep()
    tap(945, 177).sleep() //CA-5
}

//当期剿灭作战
val annihilation = OperateLevel("剿灭作战", timeout = 30L * 60L * 1000L) {
    tap(1000, 665).sleep()
    tap(835, 400).sleep()
}


/*
private fun Device.enterChipDefenderMedic() {
    tap(970, 203).sleep() //终端
    tap(822, 670).sleep() //Resource Collection
    tap(842, 329).sleep()
    tap(830, 258).sleep() //PR-X-2
}

private fun Device.enterChipSniperCaster() {
    tap(970, 203).sleep() //终端
    tap(822, 670).sleep() //Resource Collection
    tap(1060, 353).sleep()
    tap(830, 258).sleep() //PR-X-2
}

private fun Device.enterChipVanguardSupporter() {
    tap(970, 203).sleep() //终端
    tap(822, 670).sleep() //Resource Collection
    tap(1269, 350).sleep()
    tap(830, 258).sleep() //PR-X-2
}

private fun Device.enterChipGuardSpecialist() {
    tap(970, 203).sleep() //终端
    tap(822, 670).sleep() //Resource Collection
    swipe(640, 360, 640, 640, duration = 1000).sleep()
    tap(1114, 355).sleep()
    tap(830, 258).sleep() //PR-X-2
}*/
