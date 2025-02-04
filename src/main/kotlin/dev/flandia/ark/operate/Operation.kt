package dev.flandia.ark.operate

import dev.flandia.android.device.Device
import dev.flandia.android.device.TimeoutException
import dev.flandia.android.device.await
import dev.flandia.android.device.sleepl
import dev.flandia.android.util.minutes
import dev.flandia.android.util.seconds
import dev.flandia.ark.operate.OperatePoses.终端_副活动
import dev.flandia.ark.operate.OperatePoses.终端_活动
import dev.flandia.ark.operate.OperateTemplates.关卡信息界面_代理指挥关闭
import dev.flandia.ark.operate.OperateTemplates.关卡信息界面_代理指挥开启
import dev.flandia.ark.operate.OperationState.NOT_OPEN
import dev.flandia.ark.resetInterface
import java.util.concurrent.CopyOnWriteArrayList

class Operation(
    val name: String,
    val description: String,
    val timeout: Long = 5.minutes,
    val type: OperationType = OperationType.常规,
    val dropsPositions: List<Int> = listOf(0),
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
    常规, 剿灭, 活动
}

fun ActOperation(
    name: String,
    act: String,
    drop: String,
    timeout: Long = 5.minutes,
    dropsPositions: List<Int> = listOf(1),
    block: Device.() -> Unit = {}
) = Operation(name, "$act ($drop)", timeout, OperationType.活动, dropsPositions, block)

fun Device.enter(operation: Operation): OperationState {
    // 如果行动的类型是活动，则首先尝试主活动；如果无法进入，则再尝试副活动；如果均失败，则抛出异常。
    if (operation.type == OperationType.活动) {
        tap(终端_活动).sleepl()
        val result = enterOperation(operation)
        if (result != NOT_OPEN) return result

        tap(终端_副活动).sleepl()
        return enterOperation(operation)
    }
    return enterOperation(operation)
}

private fun Device.enterOperation(operation: Operation): OperationState {
    return try {
        with(operation) { enter() }
        await(关卡信息界面_代理指挥开启, 关卡信息界面_代理指挥关闭, timeout = 7.seconds)
        OperationState.OPEN
    } catch (_: TimeoutException) {
        resetInterface()
        NOT_OPEN
    }
}