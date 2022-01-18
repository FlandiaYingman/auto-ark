package top.anagke.auto_ark.operate

import top.anagke.auto_ark.tmpl

object OperateTemplates {
    // 关卡准备页面，且代理指挥开启
    internal val 关卡信息界面_代理指挥开启 by tmpl(diff = 0.01)

    // 关卡准备页面，且代理指挥关闭
    internal val 关卡信息界面_代理指挥关闭 by tmpl(diff = 0.02)

    // 等待“开始行动”
    internal val 编队界面 by tmpl(diff = 0.01)

    // 理智不足
    internal val 理智不足_可使用药剂 by tmpl(diff = 0.01)

    // 理智不足，且即将过期
    internal val 理智不足_药剂即将到期 by tmpl(diff = 0.01)

    // 理智不足，且仅能使用源石补充
    internal val 理智不足_可使用源石 by tmpl(diff = 0.01)

    // 等待“作战结束”
    internal val 行动结束 by tmpl(diff = 0.01)

    // 等待“升级”
    internal val 等级提升 by tmpl()

    internal val 剿灭_行动结束 by tmpl(diff = 0.05)
    internal val 剿灭_已通过 by tmpl(diff = 0.01)
    internal val 剿灭_已刷满 by tmpl(diff = 0.01)
}