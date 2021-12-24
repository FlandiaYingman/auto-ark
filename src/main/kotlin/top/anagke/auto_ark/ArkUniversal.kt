package top.anagke.auto_ark

import com.google.gson.Gson
import top.anagke.auto_android.AndroidActivity
import top.anagke.auto_android.Device
import top.anagke.auto_android.await
import top.anagke.auto_android.img.Img
import top.anagke.auto_android.img.Tmpl
import top.anagke.auto_android.match
import top.anagke.auto_android.matched
import top.anagke.auto_android.nap
import top.anagke.auto_android.sleep
import top.anagke.auto_android.whileNotMatch
import java.io.FileNotFoundException
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.DayOfWeek
import java.time.LocalDateTime


object ArkRes {
    operator fun invoke(name: String): URL? {
        return this.javaClass.getResource(name)
    }
}

fun template(name: String, diff: Double = 0.05): Tmpl {
    val url = ArkRes(name)
    var count = 0

    if (url == null) {
        val urlList = mutableListOf<URL>()
        do {
            val varUrl = ArkRes("${name.substringBeforeLast(".")}_${count++}.${name.substringAfterLast(".")}")
            if (varUrl != null) urlList.add(varUrl)
        } while (varUrl != null)
        if (urlList.isEmpty()) {
            throw FileNotFoundException("resource '$name' not found by '${ArkRes.javaClass.packageName}'")
        } else {
            return Tmpl(name, diff, urlList.map { Img.decode(it.readBytes())!! })
        }
    } else {
        return Tmpl(name, diff, listOf(Img.decode(url.readBytes())!!))
    }
}


val arkDayOfWeek: DayOfWeek
    get() {
        return LocalDateTime.now().minusHours(4).dayOfWeek
    }


// 主界面
val atMainScreen = template("atMainScreen.png")

// 可跳回主界面
val canJumpOut = template("canJumpOut.png", diff = 0.01)

fun Device.jumpOut() {
    if (match(canJumpOut)) {
        tap(267, 36).nap()
        tap(92, 169).nap()
        await(atMainScreen)
        sleep()
    } else {
        whileNotMatch(atMainScreen, canJumpOut) {
            back().sleep()
        }
        if (matched(canJumpOut)) {
            tap(267, 36).nap()
            tap(92, 169).nap()
            await(atMainScreen)
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