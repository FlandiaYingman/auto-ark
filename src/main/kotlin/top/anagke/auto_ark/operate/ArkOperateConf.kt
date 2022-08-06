package top.anagke.auto_ark.operate

data class ArkOperateConf(
    val 理智策略: OperateStrategy = OperateStrategy.WAIT,
    val 刷剿灭: Boolean = true,
    val 刷计划: Boolean = false,
    val 刷日常: Boolean = true,
)