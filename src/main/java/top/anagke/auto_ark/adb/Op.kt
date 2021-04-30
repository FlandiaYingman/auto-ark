@file:Suppress("unused")

package top.anagke.auto_ark.adb

import top.anagke.auto_ark.img.Img
import top.anagke.auto_ark.img.match


private val log = mu.KotlinLogging.logger { }


class OpsContext(val device: Device)

data class Tmpl(val tmpl: Img, val diff: Double, val name: String = "") {

    fun diff(img: Img): Double {
        return match(img, tmpl)
    }

    override fun toString(): String {
        return "Tmpl(name='$name', diff=$diff)"
    }

}

class TmplNotMatchException(vararg tmpls: Tmpl) : Exception("template ${tmpls.toList()} not match")

typealias Ops = OpsContext.() -> Any


fun ops(block: Ops): Ops {
    return block
}


fun OpsContext.back(delay: Int = 250) {
    device.back()
    log.info { "Back, delay $delay ms" }
    delay(delay)
}

fun OpsContext.input(str: String, delay: Int = 250) {
    device.input(str)
    log.info { "Input '$str', delay $delay ms" }
    delay(delay)
}

fun OpsContext.tap(x: Int, y: Int, delay: Int = 250) {
    device.tap(x, y)
    log.info { "Tap ($x, $y), delay $delay ms" }
    delay(delay)
}

fun OpsContext.delay(delay: Int = 250) {
    Thread.sleep(delay.toLong())
}


fun OpsContext.match(tmpl: Tmpl): Boolean {
    val diff = tmpl.diff(device.cap())
    log.info { "Match screen, template $tmpl, diff $diff" }
    return diff <= tmpl.diff
}

fun OpsContext.match(vararg tmpls: Tmpl): Tmpl? {
    tmpls.forEach { tmpl ->
        val diff = tmpl.diff(device.cap())
        log.info { "Match screen, template $tmpl, diff $diff" }
        if (diff <= tmpl.diff) return tmpl
    }
    return null
}

fun OpsContext.await(vararg tmpls: Tmpl): Tmpl {
    log.info { "Await template(s) matching ${tmpls.toList()}" }
    do {
        val cap = device.cap()
        val tmpl = tmpls.find {
            val diff = it.diff(cap)
            val matching = diff <= it.diff
            if (matching) {
                log.info { "Screen matches, template $it, diff $diff" }
            } else {
                log.debug { "Matching screen, template $it, diff $diff" }
            }
            matching
        }
        if (tmpl != null) {
            return tmpl
        }
    } while (tmpl == null)
    throw InterruptedException()
}

fun OpsContext.assert(vararg tmpls: Tmpl): Tmpl {
    log.info { "Assert template(s) matching ${tmpls.toList()}" }
    val cap = device.cap()
    val tmpl = tmpls.find {
        val diff = it.diff(cap)
        diff <= it.diff
    }
    if (tmpl != null) return tmpl
    throw TmplNotMatchException(*tmpls)
}