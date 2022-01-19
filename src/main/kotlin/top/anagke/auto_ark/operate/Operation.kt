package top.anagke.auto_ark.operate

import top.anagke.auto_android.Device
import top.anagke.auto_android.notMatch
import top.anagke.auto_android.util.minutes
import top.anagke.auto_ark.jumpOut
import top.anagke.auto_ark.operate.OperateTemplates.关卡信息界面_代理指挥关闭
import top.anagke.auto_ark.operate.OperateTemplates.关卡信息界面_代理指挥开启
import java.util.concurrent.CopyOnWriteArrayList

class Operation(
    val name: String,
    val description: String,
    val timeout: Long = 5.minutes,
    val enter: Device.() -> Unit = {},
) {

    companion object {
        val operations: MutableList<Operation> = CopyOnWriteArrayList()
    }

    init {
        operations += this
    }

    override fun toString() = "$name（$description）"

}

enum class OperationState {
    OPEN, NOT_OPEN,
}

fun Device.enter(operation: Operation): OperationState {
    with(operation) { enter() }
    if (notMatch(关卡信息界面_代理指挥开启, 关卡信息界面_代理指挥关闭)) {
        jumpOut()
        return OperationState.NOT_OPEN
    }
    return OperationState.OPEN
}