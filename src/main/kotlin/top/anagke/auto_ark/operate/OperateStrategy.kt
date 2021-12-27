package top.anagke.auto_ark.operate

/**
 * 在自动化操作时遵循的理智策略。
 */
enum class OperateStrategy {
    /**
     * 不使用任何恢复理智的道具。
     */
    WAIT,
    /**
     * 使用应急理智顶液和应急理智合剂等**非源石**道具恢复理智。
     */
    POTION,
    /**
     * 仅当即将过期时，使用应急理智顶液和应急理智合剂等**非源石**道具恢复理智。
     */
    IFF_EXPIRE_SOON,
    /**
     * 使用源石恢复理智。隐含[POTION]，即：优先使用**非原石**道具，只有当不存在**非原石**道具时，才会使用源石。
     */
    ORIGINITE;

    fun canUsePotion() = this == POTION || this == ORIGINITE
    fun canUseOriginite() = this == ORIGINITE
}