package top.anagke.auto_ark.ark

import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.await
import top.anagke.auto_ark.adb.match
import top.anagke.auto_ark.adb.nap
import top.anagke.auto_ark.adb.notMatch
import top.anagke.auto_ark.adb.sleep
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

// 可跳回主界面
val canJumpOut = template("canJumpOut.png")

fun Device.jumpOut() {
    if (match(canJumpOut)) {
        tap(267, 36).nap()
        tap(92, 169).nap()
        await(atMainScreen)
        sleep()
    } else {
        while (notMatch(atMainScreen)) {
            back().sleep()
        }
    }
}
