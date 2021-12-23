package top.anagke.auto_ark

import com.google.gson.Gson
import mu.KotlinLogging
import top.anagke.auto_android.AutoModule
import top.anagke.auto_ark.adb.Device
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.notExists

/**
 * An Arknights module which checks whether the game has newer updates.
 */
class ArkUpdate(
    private val device: Device,
    private val config: AutoArkConfig,
) : AutoModule {

    companion object {
        private val logger = KotlinLogging.logger {}

        private val arkVersionUri = URI.create(
            "https://ak-conf.hypergryph.com/config/prod/official/Android/version"
        )
    }

    private fun currentVersion(): String {
        val regex = Regex("versionName=(.*)")
        val dumpsys = device.dumpsys(config.server.activity).stdout
        val versionNumber = regex
            .find(dumpsys)
            ?.groupValues
            ?.get(1)
        return versionNumber ?: ""
    }

    private fun latestVersion(): String {
        val httpRequest = HttpRequest.newBuilder(arkVersionUri)
            .build()
        val httpResponse = HttpClient.newHttpClient()
            .send(httpRequest, BodyHandlers.ofString())

        val jsonObj = Gson().fromJson(httpResponse.body(), Map::class.java)
        val version = jsonObj["clientVersion"].toString()

        return version
    }

    private fun downloadLatest(location: Path) {
        config.server.apkUrl().openStream().use { Files.copy(it, location) }
    }

    private fun update() {
        logger.info { "检查更新" }
        val currentVersion = currentVersion()
        val latestVersion = latestVersion()
        logger.info { "检查更新，当前版本号：$currentVersion，最新版本号：$latestVersion" }
        if (currentVersion != latestVersion) {
            logger.info { "检查更新，版本号不同，更新至最新版本" }
            val apk = Path.of("./$latestVersion.apk")

            logger.info { "检查更新，下载最新版本中：$apk" }
            if (apk.notExists()) downloadLatest(apk)

            logger.info { "检查更新，安装中：$apk" }
            device.install(apk)

            logger.info { "检查更新，安装完毕：$apk，已更新至最新版本" }
        } else {
            logger.info { "检查更新，已是最新版本" }
        }
    }

    override fun run() {
        update()
    }

}
