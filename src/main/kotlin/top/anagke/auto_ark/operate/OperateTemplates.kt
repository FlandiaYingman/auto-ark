package top.anagke.auto_ark.operate

import top.anagke.auto_ark.template
import top.anagke.auto_ark.tmpl

// 关卡准备页面，且代理指挥开启
internal val atPrepareScreen = template("operate/atPrepareScreen.png", diff = 0.01)

// 关卡准备页面，且代理指挥关闭
internal val atPrepareScreen_autoDeployDisabled =
    template("operate/atPrepareScreen_autoDeployDisabled.png", diff = 0.02)

// 等待“开始行动”
internal val atFormationScreen = template("operate/atFormationScreen.png", diff = 0.01)

// 理智不足
internal val popupSanityEmpty = template("operate/popupSanityEmpty.png", diff = 0.01)

// 理智不足，且即将过期
internal val popupSanityEmptyExpireSoon = template("operate/popupSanityEmptyExpireSoon.png", diff = 0.01)

// 理智不足，且仅能使用源石补充
internal val popupSanityEmptyOriginite = template("operate/popupSanityEmptyOriginite.png", diff = 0.01)

// 等待“作战结束”
internal val atCompleteScreen = template("operate/atCompleteScreen.png", diff = 0.01)

// 等待“升级”
internal val popupLevelUp = template("operate/popupLevelUp.png")

// 等待剿灭作战“作战结束”
internal val atAnnihilationCompleteScreen = template("operate/atAnnihilationCompleteScreen.png", diff = 0.05)

object OperateTemplates {
    internal val 剿灭_已通过 by tmpl(diff = 0.01)
    internal val 剿灭_已刷满 by tmpl(diff = 0.01)
}