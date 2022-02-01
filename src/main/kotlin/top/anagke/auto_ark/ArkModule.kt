package top.anagke.auto_ark

import top.anagke.auto_android.AutoModule

abstract class ArkModule(auto: AutoArk) : AutoModule<AutoArk>(auto) {

    protected val config: AutoArkConfig = auto.config

    protected val cache: AutoArkCache = auto.cache

}