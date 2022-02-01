package top.anagke

import top.anagke.auto_ark.AutoArk
import top.anagke.auto_ark.AutoArkCache
import top.anagke.auto_ark.AutoArkConfig

fun main() {
    val config: AutoArkConfig = AutoArkConfig.loadConfig()
    val cache: AutoArkCache = AutoArkCache.loadCache(config.cacheLocation)
    config.emulator.launch().use {
        AutoArk(config, cache, it.device).doRoutine()
    }
}