package dev.flandia.ark.operate

data class ArkOperateConf(
    val 理智策略: OperateStrategy,
    val 刷剿灭: Boolean,
    val 刷计划: Boolean,
    val 刷日常: Boolean,
    val 资源关种类: 资源关种类
)

enum class 资源关种类 {
    LEVEL_6,
    LEVEL_5
}