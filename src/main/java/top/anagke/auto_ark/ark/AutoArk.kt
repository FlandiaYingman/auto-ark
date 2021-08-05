package top.anagke.auto_ark.ark

import top.anagke.auto_ark.adb.Device

fun dailyRoutine(device: Device) {
    device.login()
    device.autoRiic()
    device.autoRecruit()
    device.autoOperate()
    device.autoMission()
}