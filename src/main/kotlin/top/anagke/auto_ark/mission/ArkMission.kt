package top.anagke.auto_ark.mission

import top.anagke.auto_android.device.assert
import top.anagke.auto_android.device.await
import top.anagke.auto_android.device.match
import top.anagke.auto_android.device.sleep
import top.anagke.auto_ark.*

class ArkMission(
    auto: AutoArk,
) : ArkModule(auto) {

    companion object {
        // 仍有任务奖励尚未领取
        private val 可收集 by tmpl()
    }

    override val name = "任务模块"

    override fun run(): Unit = device.run {
        assert(主界面)

        tap(830, 603).sleep() // 任务

        tap(665, 40).sleep() // 日常任务（防止进入见习任务）
        if (match(可收集)) {
            tap(1115, 150).sleep() //领取所有奖励
            tap(1115, 150).sleep() //确认
            tap(1115, 150).sleep() //确认
        }

        tap(868, 36).sleep() //周常任务
        if (match(可收集)) {
            tap(1115, 150).sleep() //领取所有奖励
            tap(1115, 150).sleep() //确认
            tap(1115, 150).sleep() //确认
        }

        resetInterface()
        await(主界面)
    }

}
