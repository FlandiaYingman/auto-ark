package top.anagke.auto_ark.operate

import kotlinx.serialization.Serializable

@Serializable
data class OperateConfig(
    val strategy: OperateStrategy = OperateStrategy.WAIT,
    val doFarmAnnihilation: Boolean = true,
    val doFarmPlan: Boolean = false,
    val doFarmDaily: Boolean = true,
    val farmingPlan: MutableMap<String, Int> = OperateLevel.levels
        .map(OperateLevel::name)
        .associateWith { 0 }
        .toMutableMap(),
)