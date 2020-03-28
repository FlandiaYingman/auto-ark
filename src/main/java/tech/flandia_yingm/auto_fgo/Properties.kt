package tech.flandia_yingm.auto_fgo

import java.io.File
import java.util.Properties

object Properties {

    private val properties = Properties().apply {
        put("adb.serial", "127.0.0.1:7555")
        put("arknights.username", "")
        put("arknights.password", "")
        put("arknights.nemuPath", "")
        put("arknights.nemuPackage", "com.hypergryph.arknights")
    }

    private val propertiesFile = File("auto-fgo.properties")

    init {
        if (!propertiesFile.exists()) {
            propertiesFile.createNewFile()
            propertiesFile.outputStream().use {
                properties.store(it, null)
            }
        }
        propertiesFile.inputStream().use {
            properties.load(it)
        }
    }

    val adbSerial = properties["adb.serial"] as String
    val arknightsUsername = properties["arknights.username"] as String
    val arknightsPassword = properties["arknights.password"] as String
    val arknightsNemuPath = properties["arknights.nemuPath"] as String
    val arknightsNemuPackage = properties["arknights.nemuPackage"] as String

}