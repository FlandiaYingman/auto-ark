package dev.flandia.ark.update

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.net.URL

object ArkUrls {

    val arkVersionUrl: URL
        get() {
            return URL("https://ak-conf.hypergryph.com/config/prod/official/Android/version")
        }

    val officialApkUrl: URL
        get() {
            return URL("https://ak.hypergryph.com/downloads/android_lastest")
        }

    val bilibiliApkUrl: URL
        get() {
            val metadataUrl = URL("https://line1-h5-pc-api.biligame.com/game/detail/gameinfo?game_base_id=101772")
            val metadata = metadataUrl.openStream().use { Gson().fromJson(it.reader(), JsonObject::class.java) }
            val url = metadata
                .get("data").asJsonObject
                .get("android_download_link").asString
            return URL(url)
        }

}