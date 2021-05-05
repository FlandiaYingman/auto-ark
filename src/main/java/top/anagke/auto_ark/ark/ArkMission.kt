package top.anagke.auto_ark.ark

import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.Ops
import top.anagke.auto_ark.adb.assert
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.back
import top.anagke.auto_ark.adb.delay
import top.anagke.auto_ark.adb.notMatch
import top.anagke.auto_ark.adb.ops
import top.anagke.auto_ark.adb.tap
import top.anagke.auto_ark.autoProps


// 任务领取完毕
private val ifRewardEmpty = template("mission/ifRewardEmpty.png")

fun main() {
    Device(autoProps.adbHost, autoProps.adbPort)(autoMission())
}

/**
 * 自动化领取所有任务奖励。
 *
 * 开始于：主界面。
 * 结束于：主界面。
 */
fun autoMission(): Ops {
    return ops {
        assert(atMainScreen)

        tap(830, 603) // 任务
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
}