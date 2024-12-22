package dev.flandia.ark

import dev.flandia.android.AutoInterruptedException
import dev.flandia.android.device.*
import dev.flandia.android.img.Img
import dev.flandia.android.img.Tmpl
import java.time.DayOfWeek
import java.time.LocalDateTime
import kotlin.reflect.KProperty

fun tmpl(diff: Double = 0.01) = TmplDelegate(diff)

class TmplDelegate(private val diff: Double) {

    private var tmpl: Tmpl? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Tmpl {
        if (tmpl == null) {
            val name = property.name
            val kClass = thisRef?.let { it::class.java } ?: AutoArk::class.java
            tmpl = findSingle(name, kClass) ?: findMultiple(name, kClass)
                    ?: throw Exception("cannot find template with given name '$name'")
        }
        return tmpl!!
    }

    private fun findSingle(name: String, kClass: Class<*>): Tmpl? {
        return kClass
            .getResource("${name}.png")
            ?.readBytes()
            ?.let { Tmpl(name, diff, listOf(Img.decode(it)!!)) }
    }

    private fun findMultiple(name: String, kClass: Class<*>): Tmpl? {
        val imgs = mutableListOf<Img>()
        var i = 0
        while (true) {
            val img = kClass
                .getResource("${name}_${i++}.png")
                ?.readBytes()
                ?.let { Img.decode(it) }
            if (img == null) {
                break
            } else {
                imgs += img
            }
        }
        if (imgs.isEmpty()) return null
        return Tmpl("${name}.png", diff, imgs)
    }

}

fun today(): DayOfWeek {
    return LocalDateTime.now().minusHours(4).dayOfWeek
}


// 开始界面
val 开始界面 by tmpl()

// 登录认证已失效
val 登录认证失效 by tmpl()

// 主界面
val 主界面 by tmpl(diff = 0.05)

// 可跳回主界面
val 可跳出 by tmpl()


fun Device.resetInterface() {
    if (match(可跳出)) {
        tap(267, 36).nap()
        tap(92, 169).nap()
        await(主界面)
    } else {
        whileNotMatch(主界面, 可跳出, 登录认证失效) {
            back().sleep()
        }
        if (matched(登录认证失效)) throw AutoInterruptedException("登录认证失效")
        if (matched(可跳出)) resetInterface()
    }
}

enum class ArkServer(
    val activity: AndroidActivity,
) {
    OFFICIAL(AndroidActivity("com.hypergryph.arknights", "com.u8.sdk.U8UnityContext")),
//    BILIBILI(AndroidActivity("com.hypergryph.arknights.bilibili", ""))
}
