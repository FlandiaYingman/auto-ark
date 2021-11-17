package top.anagke.auto_ark.store

import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.assert
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.match
import top.anagke.auto_ark.adb.nap
import top.anagke.auto_ark.adb.sleep
import top.anagke.auto_ark.adb.whileNotMatch
import top.anagke.auto_ark.AutoArk
import top.anagke.auto_ark.appConfig
import top.anagke.auto_ark.atMainScreen
import top.anagke.auto_ark.jumpOut
import top.anagke.auto_ark.template
import top.anagke.auto_android.img.Tmpl
import top.anagke.auto_android.util.Pos

class ArkStore(
    private val device: Device,
) {

    companion object {
        private val isCreditGained: Tmpl = template("store/isCreditGained.png")
        private val isAtCreditBuyingDialog: Tmpl = template("store/isAtCreditBuyingDialog.png")
    }


    fun auto() = device.apply {
        assert(atMainScreen)

        enterStore()
        autoCreditStore()
        jumpOut()
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
        whileNotMatch(isCreditGained) {
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
            if (match(isAtCreditBuyingDialog)) {
                //提示框未弹出，信用点不足
                back().nap()
                break
            }
        }
        await(isCreditGained) //默认已收取信用点
    }

}

fun main() {
    AutoArk(appConfig).autoStore()
}