package top.anagke.auto_ark.ark

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
    }

    fun auto() {
        update()
    }

    private val arkUpdateUrl = URI.create("https://ak.hypergryph.com/downloads/android_lastest")

    fun checkUpdate(): String {
        val request = HttpRequest.newBuilder()
            .uri(arkUpdateUrl)
            .method("HEAD", HttpRequest.BodyPublishers.noBody())
            .build()
        val response = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString())
        val location = response.headers().firstValue("Location")
        val uri = URI.create(location.orElseThrow { NullPointerException() })
        val name = uri.path.substringAfterLast('/')
        return name
    }

    fun update() {
        logger.info { "检查更新" }
        val name = checkUpdate()
        logger.info { "检查更新完毕，最新版本号：：$name" }
        if (config.arkVersion.isEmpty()) {
            logger.info { "当前版本号为空，修改为：$name" }
            config.arkVersion = name
            return
        }
        if (config.arkVersion != name) {
            logger.info { "当前版本号为：${config.arkVersion}，更新至：$name" }
            val downloadFile = Path(name)
            logger.info { "下载中：$name" }
            if (downloadFile.notExists()) {
                arkUpdateUrl.toURL().openStream().use {
                    Files.copy(it, downloadFile)
                }
            }
            logger.info { "安装中：$name" }
            device.install(downloadFile)
            config.arkVersion = name
            logger.info { "安装完毕：$name" }
        }
    }

}

fun main(args: Array<String>) {
    ArkUpdate(Device(), appConfig).checkUpdate()
}
