package dev.flandia.ark

import dev.flandia.android.AutoModule

abstract class ArkModule(auto: AutoArk) : AutoModule<AutoArk>(auto) {

    protected val config: AutoArkConfig = auto.config

    protected val savedata: AutoArkSavedata = auto.savedata

}