package tech.flandia_yingm.auto_fgo.arknights

import tech.flandia_yingm.auto_fgo.Properties
import java.io.File
import java.net.InetAddress
import java.net.Socket

object ArknightsNative {

    private val nemuPath = File(Properties.arknightsNemuPath).absolutePath
    private val nemuPackage = Properties.arknightsNemuPackage

    fun startNemu() {
        val cmd = """"$nemuPath" -p $nemuPackage"""

    }

    fun stopNemu() {
    }

}