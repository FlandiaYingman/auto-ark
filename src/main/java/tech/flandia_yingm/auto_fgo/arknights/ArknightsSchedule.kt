package tech.flandia_yingm.auto_fgo.arknights

import tech.flandia_yingm.auto_fgo.device.android.AdbDevice

fun main() {
    ArknightsNative.startNemu()
    val emu = AdbDevice.connect("127.0.0.1:7555")

    val loginAuto = ArknightsLoginAuto(emu)
    loginAuto.skipLogo()
    loginAuto.login(ArknightsAccount("13046875179", "F1andiaYingM"))

    val infrastructureAuto = ArknightsInfrastructureAuto(emu)
    infrastructureAuto.enterInfrastructure()
    infrastructureAuto.harvestProduct()
    infrastructureAuto.harvestTrade()
    infrastructureAuto.exitInfrastructure()

}

