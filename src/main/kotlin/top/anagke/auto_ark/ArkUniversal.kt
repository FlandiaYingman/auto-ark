package top.anagke.auto_ark

import top.anagke.auto_android.device.*
import top.anagke.auto_android.img.Img
import top.anagke.auto_android.img.Tmpl
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
    OFFICIAL(AndroidActivity("com.hypergryph.arknights", "com.u8.sdk.U8UnityContext")),
    BILIBILI(AndroidActivity("com.hypergryph.arknights.bilibili", ""))
}