package top.anagke.auto_ark.mission

import top.anagke.auto_android.AutoModule
import top.anagke.auto_android.Device
import top.anagke.auto_android.assert
import top.anagke.auto_android.await
import top.anagke.auto_android.match
import top.anagke.auto_android.sleep
import top.anagke.auto_ark.atMainScreen
import top.anagke.auto_ark.jumpOut
import top.anagke.auto_ark.template

class ArkMission(
    private val device: Device,
) : AutoModule {

    companion object {
        // 仍有任务奖励尚未领取
        private val hasReward = template("mission/hasReward.png")
    }

    override fun run(): Unit = device.run {
        assert(atMainScreen)

        tap(830, 603).sleep() // 任务

        tap(665, 40).sleep() // 日常任务（防止进入见习任务）
        if (match(hasReward)) {
            tap(1115, 150).sleep() //领取所有奖励
            tap(1115, 150).sleep() //确认
            tap(1115, 150).sleep() //确认
        }

        tap(868, 36).sleep() //周常任务
        if (match(hasReward)) {
            tap(1115, 150).sleep() //领取所有奖励
            tap(1115, 150).sleep() //确认
            tap(1115, 150).sleep() //确认
        }

        jumpOut()
        await(atMainScreen)
    }

}
