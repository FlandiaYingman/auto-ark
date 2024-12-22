package dev.flandia.ark.store

import dev.flandia.android.device.*
import dev.flandia.android.img.Pos
import dev.flandia.ark.*

class ArkStore(
    auto: AutoArk,
) : ArkModule(auto) {

    companion object {
        private val 已领取信用点 by tmpl()
        private val 购买物品提示框 by tmpl()
    }

    override val name: String = "商店模块"


    override fun run(): Unit = device.run {
        assert(主界面)

        enterStore()
        autoCreditStore()
        resetInterface()
    }

    private fun enterStore() = device.apply {
        tap(832, 479).sleep()
    }

    fun autoCreditStore() = device.apply {
        enterCreditStore()
        gainCredit()
        consumeCredit()
    }

    private fun enterCreditStore() = device.apply {
        tap(1198, 106).sleep()
    }

    private fun gainCredit() = device.apply {
        whileNotMatch(已领取信用点) {
            tap(1026, 39).sleep()
        }
    }

    private fun consumeCredit() = device.apply {
        val commodities = listOf(
            Pos(129, 274),
            Pos(384, 271),
            Pos(638, 270),
            Pos(894, 267),
            Pos(1147, 274),
            Pos(134, 527),
            Pos(386, 522),
            Pos(650, 527),
            Pos(909, 524),
            Pos(1153, 525),
        )
        for ((x, y) in commodities) {
            tap(x, y).nap() //触碰商品
            tap(873, 577).sleep() //确认“购买物品”

            tap(x, y).nap() //关闭弹出的提示框（点击原商品位置，防止触碰其他位置）
            tap(x, y).nap() //关闭弹出的提示框（点击原商品位置，防止触碰其他位置）

            //两次，防止在确认”购买物品“关闭后，对话框出现前的一瞬间检测到
            if (match(购买物品提示框)) {
                //提示框未弹出，信用点不足
                back().nap()
                break
            }
        }
        await(已领取信用点) //默认已收取信用点
    }

}