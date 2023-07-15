package top.anagke.auto_ark.recruit

import top.anagke.auto_ark.tmpl

object RecruitTemplates {
    // 公开招募界面
    internal val 公开招募界面 by tmpl()

    // 能够刷新TAG
    internal val 可刷新标签 by tmpl()
    internal val 公开招募面板 by tmpl()

    // 公开招募1号栏位可用
    internal val 招募槽1空闲 by tmpl(diff = 0.02)
    internal val 招募槽1完成 by tmpl(diff = 0.02)
    internal val 招募槽1招募中 by tmpl(diff = 0.02)

    // 公开招募2号栏位可用
    internal val 招募槽2空闲 by tmpl(diff = 0.02)
    internal val 招募槽2完成 by tmpl(diff = 0.02)
    internal val 招募槽2招募中 by tmpl(diff = 0.02)

    // 公开招募3号栏位可用
    internal val 招募槽3空闲 by tmpl(diff = 0.02)
    internal val 招募槽3完成 by tmpl(diff = 0.02)
    internal val 招募槽3招募中 by tmpl(diff = 0.02)

    // 公开招募4号栏位可用
    internal val 招募槽4空闲 by tmpl(diff = 0.02)
    internal val 招募槽4完成 by tmpl(diff = 0.02)
    internal val 招募槽4招募中 by tmpl(diff = 0.02)
}