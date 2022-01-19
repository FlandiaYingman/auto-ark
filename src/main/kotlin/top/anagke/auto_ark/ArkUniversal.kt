package top.anagke.auto_ark

import com.google.gson.Gson
import top.anagke.auto_android.*
import top.anagke.auto_android.img.Img
import top.anagke.auto_android.img.Tmpl
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.DayOfWeek
import java.time.LocalDateTime
import kotlin.reflect.KProperty

fun tmpl(diff: Double = 0.05) = TmplDelegate(diff)

class TmplDelegate(private val diff: Double) {
    private var tmpl: Tmpl? = null
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Tmpl {
        if (tmpl == null) {
            val name = "${property.name}.png"
            val kClass = thisRef?.let { it::class.java } ?: AutoArk::class.java
            val tmplBytes = kClass
                .getResource(name)!!
                .readBytes()
            tmpl = Tmpl(name, diff, Img.decode(tmplBytes)!!)
        }
        return tmpl!!
    }
}

fun today(): DayOfWeek {
    return LocalDateTime.now().minusHours(4).dayOfWeek
}


// 主界面
val 主界面 by tmpl()

// 可跳回主界面
val 可跳出 by tmpl(diff = 0.01)

fun Device.jumpOut() {
    if (match(可跳出)) {
        tap(267, 36).nap()
        tap(92, 169).nap()
        await(主界面)
        sleep()
    } else {
        whileNotMatch(主界面, 可跳出) {
            back().sleep()
        }
        if (matched(可跳出)) {
            tap(267, 36).nap()
            tap(92, 169).nap()
            await(主界面)
            sleep()
        }
    }
}


enum class ArkServer(
    val activity: AndroidActivity,
) {
    OFFICIAL(AndroidActivity("com.hypergryph.arknights", "com.u8.sdk.U8UnityContext")) {
        override fun apkUrl(): URL =
            URL("https://ak.hypergryph.com/downloads/android_lastest")
    },

    BILIBILI(AndroidActivity("com.hypergryph.arknights.bilibili", "")) {
        override fun apkUrl(): URL {
            val versionDataUrl =
                URI.create("https://line1-h5-pc-api.biligame.com/game/detail/gameinfo?game_base_id=101772")
            val request = HttpRequest.newBuilder()
                .uri(versionDataUrl)
                .GET()
                .build()
            val response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString())

            val jsonObject = Gson().fromJson(response.body(), Map::class.java)
            val data = jsonObject["data"] as Map<*, *>
            return URL(data["android_download_link"].toString())
        }
    };

    abstract fun apkUrl(): URL
}