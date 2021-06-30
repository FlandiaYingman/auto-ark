package top.anagke.auto_ark.ark

import kotlinx.serialization.Serializable
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.ark.ArkLoginContext.OfficialLogin
import top.anagke.auto_ark.img.Img
import top.anagke.auto_ark.img.Tmpl
import java.io.FileNotFoundException
import java.net.URL


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
            return Tmpl(name, diff, urlList.map { Img(it.readBytes()) })
        }
    } else {
        return Tmpl(name, diff, listOf(Img(url.readBytes())))
    }
}

// 主界面
val atMainScreen = template("atMainScreen.png", diff = 0.06)