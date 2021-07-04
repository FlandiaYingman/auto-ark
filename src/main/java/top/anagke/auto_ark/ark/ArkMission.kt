package top.anagke.auto_ark.ark

import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.assert
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.delay
import top.anagke.auto_ark.adb.match
import top.anagke.auto_ark.adb.sleep

// 仍有任务奖励尚未领取
private val hasReward = template("mission/hasReward.png")

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
    if (match(hasReward)) {
        tap(1115, 150).sleep() //领取所有奖励
        tap(1115, 150).sleep() //确认
    }

    tap(868, 36) //周常任务
    if (match(hasReward)) {
        tap(1115, 150).sleep() //领取所有奖励
        tap(1115, 150).sleep() //确认
    }

    back()
    await(atMainScreen)
}
