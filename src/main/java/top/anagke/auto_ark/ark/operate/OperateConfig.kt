package top.anagke.auto_ark.ark.operate

import kotlinx.serialization.Serializable

@Serializable
data class OperateConfig(
    val strategy: OperateStrategy = OperateStrategy.WAIT,
    val doFarmDaily: Boolean = true,
    val doFarmAnnihilation: Boolean = true,
    val farmingPlan: MutableMap<String, Int> = OperateLevel.levels
        .map(OperateLevel::name)
        .associateWith { 0 }
        .toMutableMap(),
)