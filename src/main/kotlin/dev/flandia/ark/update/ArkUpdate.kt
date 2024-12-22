package dev.flandia.ark.update

import com.google.gson.Gson
import com.google.gson.JsonObject

import org.tinylog.kotlin.Logger
import dev.flandia.ark.ArkModule
import dev.flandia.ark.ArkServer
import dev.flandia.ark.AutoArk
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.Instant
import kotlin.io.path.deleteExisting
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.notExists

/** An Arknights module which checks whether the game has newer updates. */
class ArkUpdate(auto: AutoArk) : ArkModule(auto) {

    companion object;

    override val name = "更新模块"

    private fun currentVersion(): String {
        val regex = Regex("versionName=(.*)")
        val dumpsys = dumpsys()
        val versionNumber = regex.find(dumpsys)?.groupValues?.get(1)
        return versionNumber ?: ""
    }

    private fun dumpsys(): String {
        val timeLimit = Instant.now() + Duration.ofSeconds(30)
        while (Instant.now() < timeLimit) {
            val dumpsys = device.dumpsys(config.服务器.activity)
            if (dumpsys.stderr.isNotBlank()) {
                Logger.info("获取明日方舟包信息错误")
                continue
            }
            if (dumpsys.stdout.isBlank()) {
                Logger.info("获取明日方舟包信息输出为空")
                continue
            }
            return dumpsys.stdout
        }
        throw IllegalStateException("获取明日方舟包信息失败，已达到超时时间")
    }

    private fun latestVersion(): String {
        val response = dev.flandia.ark.update.ArkUrls.arkVersionUrl.openStream().use { Gson().fromJson(it.reader(), JsonObject::class.java) }
        val version = response.get("clientVersion").asString

        return version
    }


    private fun cleanup(dir: Path) {
        dir.listDirectoryEntries("*.apk").forEach { it.deleteExisting() }
    }

    private fun downloadLatest(location: Path) {
        val url = when (config.服务器) {
            ArkServer.OFFICIAL -> dev.flandia.ark.update.ArkUrls.officialApkUrl
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
            cleanup(Path.of("./"))
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
