package top.anagke.auto_ark

import com.google.gson.Gson
import mu.KotlinLogging
import top.anagke.auto_ark.adb.Device
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.notExists

/**
 * An Arknights module which checks whether the game has newer updates.
 */
class ArkUpdate(
    val device: Device,
    val config: AutoArkConfig,
) {

    companion object {
        private val logger = KotlinLogging.logger {}

        private val arkUpdateUrl =
            URI.create("https://ak.hypergryph.com/downloads/android_lastest")
        private val arkBilibiliUpdateUrl =
            URI.create("http://line1-h5-pc-api.biligame.com/game/detail/gameinfo?game_base_id=101772&_=1638323032944")
    }

    fun auto() {
        update()
    }

    private fun checkUpdate(): Pair<URI, String> {
        val request = HttpRequest.newBuilder()
            .uri(arkUpdateUrl)
            .method("HEAD", HttpRequest.BodyPublishers.noBody())
            .build()
        val response = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString())
        val location = response.headers().firstValue("Location")
        val uri = URI.create(location.orElseThrow { NullPointerException() })
        val name = uri.path.substringAfterLast('/')
        return arkUpdateUrl to name
    }

    private fun checkUpdateBilibili(): Pair<URI, String> {
        val request = HttpRequest.newBuilder()
            .uri(arkBilibiliUpdateUrl)
            .GET()
            .build()
        val response = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString())

        val jsonObject = Gson().fromJson(response.body(), Map::class.java)
        val data = jsonObject["data"] as Map<*, *>
        return URI.create(data["android_download_link"].toString()) to "${data["android_pkg_ver"]}.apk"
    }

    private fun update() {
        logger.info { "检查更新" }
        val (url, ver) = if (config.isBilibili) checkUpdateBilibili() else checkUpdate()
        logger.info { "检查更新完毕，最新版本号：$ver" }
        if (config.arkVersion.isEmpty()) {
            logger.info { "当前版本号为空，修改为：$ver" }
            config.arkVersion = ver
            return
        }
        if (config.arkVersion != ver) {
            logger.info { "当前版本号为：${config.arkVersion}，更新至：$ver" }
            val downloadFile = Path(ver)
            logger.info { "下载中：$ver" }
            if (downloadFile.notExists()) {
                url.toURL().openStream().use {
                    Files.copy(it, downloadFile)
                }
            }
            logger.info { "安装中：$ver" }
            device.install(downloadFile)
            config.arkVersion = ver
            logger.info { "安装完毕：$ver" }
        }
    }

}
