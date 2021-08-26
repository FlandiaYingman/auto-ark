package top.anagke.auto_ark.ark

import mu.KotlinLogging
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.ark.operate.autoOperate
import java.time.DayOfWeek
import java.time.LocalDateTime

val log = KotlinLogging.logger {}

fun dailyRoutine(device: Device) {
    device.login()
    device.autoRiic()
    device.autoRecruit()
    device.autoOperate()
    device.autoMission()
}

val arkToday: DayOfWeek get() = LocalDateTime.now().minusHours(4).dayOfWeek