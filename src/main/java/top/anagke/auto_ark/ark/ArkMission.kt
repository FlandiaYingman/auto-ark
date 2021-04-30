package top.anagke.auto_ark.ark

import top.anagke.auto_ark.adb.Ops
import top.anagke.auto_ark.adb.assert
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.back
import top.anagke.auto_ark.adb.delay
import top.anagke.auto_ark.adb.match
import top.anagke.auto_ark.adb.ops
import top.anagke.auto_ark.adb.tap


// 任务领取完毕
val isMissionRewardEmpty by template("isMissionRewardEmpty.png")

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
        while (!match(isMissionRewardEmpty)) {
            tap(1115, 150) //领取奖励
        }
        delay(1000)

        tap(937, 34) //周常任务
        while (!match(isMissionRewardEmpty)) {
            tap(1115, 150) //领取奖励
        }
        delay(1000)

        back()
        await(atMainScreen)
    }
}