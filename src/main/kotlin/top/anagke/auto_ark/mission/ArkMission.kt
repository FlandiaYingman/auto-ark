package top.anagke.auto_ark.mission

import top.anagke.auto_android.*
import top.anagke.auto_ark.jumpOut
import top.anagke.auto_ark.tmpl
import top.anagke.auto_ark.主界面

class ArkMission(
    private val device: Device,
) : AutoModule {

    companion object {
        // 仍有任务奖励尚未领取
        private val 可收集 by tmpl()
    }

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

        jumpOut()
        await(主界面)
    }

}
