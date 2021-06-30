package top.anagke.auto_ark.ark

import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.assert
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.delay
import top.anagke.auto_ark.adb.nap
import top.anagke.auto_ark.adb.notMatch

// 任务领取完毕
private val ifRewardEmpty = template("mission/ifRewardEmpty.png")


/**
 * 自动化领取所有任务奖励。
 *
 * 开始于：主界面。
 * 结束于：主界面。
 */
fun Device.autoMission() {
    assert(atMainScreen)

    tap(830, 603).delay(500) // 任务
    tap(665, 40) // 日常任务（防止进入见习任务）
    while (notMatch(ifRewardEmpty)) {
        tap(1115, 150) //领取奖励
    }
    delay(1000)

    tap(868, 36) //周常任务
    while (notMatch(ifRewardEmpty)) {
        tap(1115, 150) //领取奖励
    }
    delay(1000)

    back()
    await(atMainScreen)
}

fun main() {
    Device().autoMission()
}