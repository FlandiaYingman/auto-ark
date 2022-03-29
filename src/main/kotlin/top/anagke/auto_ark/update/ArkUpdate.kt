package top.anagke.auto_ark.update

import com.google.gson.Gson
import com.google.gson.JsonObject

import org.tinylog.kotlin.Logger
import top.anagke.auto_ark.ArkModule
import top.anagke.auto_ark.ArkServer
import top.anagke.auto_ark.AutoArk
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.notExists

/**
 * An Arknights module which checks whether the game has newer updates.
 */
class ArkUpdate(auto: AutoArk) : ArkModule(auto) {

    companion object;

    override val name = "更新模块"

    private fun currentVersion(): String {
        val regex = Regex("versionName=(.*)")
        val dumpsys = device.dumpsys(config.server.activity).stdout
        val versionNumber = regex.find(dumpsys)?.groupValues?.get(1)
        return versionNumber ?: ""
    }

    private fun latestVersion(): String {
        val response = ArkUrls.arkVersionUrl.openStream().use { Gson().fromJson(it.reader(), JsonObject::class.java) }
        val version = response.get("clientVersion").asString

        return version
    }

    private fun downloadLatest(location: Path) {
        val url = when (config.server) {
            ArkServer.OFFICIAL -> ArkUrls.officialApkUrl
            ArkServer.BILIBILI -> ArkUrls.bilibiliApkUrl
        }
        url.openStream().use {
            Files.copy(it, location)
        }
    }

    private fun update() {
        Logger.info("检查更新")
        val currentVersion = currentVersion()
        val latestVersion = latestVersion()
        Logger.info("检查更新，当前版本号：$currentVersion，最新版本号：$latestVersion")
        if (currentVersion != latestVersion) {
            Logger.info("检查更新，版本号不同，更新至最新版本")
            val apkFile = Path.of("./$latestVersion.apk")

            Logger.info("检查更新，下载最新版本中：$apkFile")
            if (apkFile.notExists()) {
                downloadLatest(apkFile)
            }

            Logger.info("检查更新，安装中：$apkFile")
            device.install(apkFile.toString())

            Logger.info("检查更新，安装完毕：$apkFile，已更新至最新版本")
        } else {
            Logger.info("检查更新，已是最新版本")
        }
    }

    override fun run() {
        update()
    }

}
