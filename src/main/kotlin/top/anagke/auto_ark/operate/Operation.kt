package top.anagke.auto_ark.operate

import top.anagke.auto_android.device.Device
import top.anagke.auto_android.device.TimeoutException
import top.anagke.auto_android.device.await
import top.anagke.auto_android.device.notMatch
import top.anagke.auto_android.util.minutes
import top.anagke.auto_android.util.seconds
import top.anagke.auto_ark.operate.OperateTemplates.关卡信息界面_代理指挥关闭
import top.anagke.auto_ark.operate.OperateTemplates.关卡信息界面_代理指挥开启
import top.anagke.auto_ark.resetInterface
import java.util.concurrent.CopyOnWriteArrayList

class Operation(
    val name: String,
    val description: String,
    val timeout: Long = 5.minutes,
    val type: OperationType = OperationType.常规,
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

enum class OperationType {
    常规, 剿灭
}

fun Device.enter(operation: Operation): OperationState {
    with(operation) { enter() }
    try {
        await(关卡信息界面_代理指挥开启, 关卡信息界面_代理指挥关闭, timeout = 5.seconds)
    } catch (_: TimeoutException) {
    }
    if (notMatch(关卡信息界面_代理指挥开启, 关卡信息界面_代理指挥关闭)) {
        resetInterface()
        return OperationState.NOT_OPEN
    }
    return OperationState.OPEN
}