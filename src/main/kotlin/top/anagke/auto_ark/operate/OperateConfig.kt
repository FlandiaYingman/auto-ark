package top.anagke.auto_ark.operate

import kotlinx.serialization.Serializable

@Serializable
data class OperateConfig(
    val strategy: OperateStrategy = OperateStrategy.WAIT,
    val doFarmAnnihilation: Boolean = true,
    val doFarmPlan: Boolean = false,
    val doFarmDaily: Boolean = true,
)