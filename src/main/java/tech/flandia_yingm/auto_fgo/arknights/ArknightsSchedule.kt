package tech.flandia_yingm.auto_fgo.arknights

import tech.flandia_yingm.auto_fgo.Properties
import tech.flandia_yingm.auto_fgo.device.android.AdbDevice

fun main() {

    ArknightsNative.startNemu()
    val emu = AdbDevice.connect(Properties.adbSerial)

    val loginAuto = ArknightsAuto(emu)
    loginAuto.skipLogo()
    loginAuto.login(ArknightsAccount(Properties.arknightsUsername, Properties.arknightsPassword))

    val infrastructureAuto = ArknightsAuto(emu)
    infrastructureAuto.enterInfrastructure()
    infrastructureAuto.harvestProduct()
    infrastructureAuto.harvestTrade()
    infrastructureAuto.exitInfrastructure()

}