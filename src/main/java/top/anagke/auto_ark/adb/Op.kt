@file:Suppress("unused")

package top.anagke.auto_ark.adb

import top.anagke.auto_ark.img.Img
import top.anagke.auto_ark.img.match


private val log = mu.KotlinLogging.logger { }


open class OpsContext(val device: Device) {

    var lastMatchedTemplate: Tmpl? = null
    var lastMatchDiff: Double? = null

    operator fun <T> invoke(opsType: OpsType<T>): T = device.invoke(opsType)

}

data class Tmpl(val tmpls: List<Img>, val threshold: Double, val name: String = "") {

    constructor(tmpl: Img, diff: Double, name: String = "") : this(listOf(tmpl), diff, name)

    fun diff(img: Img): Double {
        return tmpls.minOf { match(img, it) }
    }

    override fun toString(): String {
        return "Tmpl($name)"
    }

}


class TmplNotMatchException(vararg tmpls: Tmpl) : Exception("template ${tmpls.toList()} not match")

typealias OpsType<T> = OpsContext.() -> T
typealias Ops = OpsType<Unit>


fun ops(block: Ops): Ops {
    return block
}

fun <T> opsType(block: OpsType<T>): OpsType<T> {
    return block
}


fun OpsContext.back(delay: Int = 250) {
    device.back()
    log.debug { "Back, delay $delay ms" }
    delay(delay)
}

fun OpsContext.input(str: String, delay: Int = 250) {
    device.input(str)
    log.debug { "Input '$str', delay $delay ms" }
    delay(delay)
}

fun OpsContext.tap(x: Int, y: Int, delay: Int = 250) {
    device.tap(x, y)
    log.debug { "Tap ($x, $y), delay $delay ms" }
    delay(delay)
}

fun OpsContext.delay(delay: Int = 250) {
    Thread.sleep(delay.toLong())
}


fun OpsContext.match(tmpl: Tmpl): Boolean {
    val diff = tmpl.diff(device.cap())
    val result = diff <= tmpl.threshold
    this.lastMatchedTemplate = if (result) tmpl else null
    this.lastMatchDiff = diff
    log.debug { "Match, result=$result, diff=${diff.formatDiff()}, $tmpl" }
    return result
}

fun OpsContext.notMatch(tmpl: Tmpl): Boolean {
    return match(tmpl).not()
}

fun OpsContext.which(vararg tmpls: Tmpl): Tmpl? {
    val screen = device.cap()
    tmpls.forEach { tmpl ->
        val diff = tmpl.diff(screen)
        val result = diff <= tmpl.threshold
        this.lastMatchedTemplate = if (result) tmpl else null
        this.lastMatchDiff = diff
        log.debug { "Match, result=$result, diff=${diff.formatDiff()}, $tmpl" }
        if (result) return tmpl
    }
    return null
}


fun OpsContext.await(vararg tmpls: Tmpl): Tmpl {
    log.debug { "Await matching ${tmpls.toList()}" }
    do {
        val screen = device.cap()
        val result = tmpls.find { tmpl ->
            val diff = tmpl.diff(screen)
            val result = diff <= tmpl.threshold
            this.lastMatchedTemplate = if (result) tmpl else null
            this.lastMatchDiff = diff
            log.debug { "Match, result=$result, diff=${diff.formatDiff()}, $tmpl" }
            result
        }
        if (result != null) {
            return result
        }
    } while (result == null)
    throw InterruptedException()
}

fun OpsContext.assert(vararg tmpls: Tmpl): Tmpl {
    log.debug { "Assert matching ${tmpls.toList()}" }
    val screen = device.cap()
    val result = tmpls.find { tmpl ->
        val diff = tmpl.diff(screen)
        val result = diff <= tmpl.threshold
        this.lastMatchedTemplate = if (result) tmpl else null
        this.lastMatchDiff = diff
        log.debug { "Match, result=$result, diff=${diff.formatDiff()}, $tmpl" }
        result
    }
    if (result != null) {
        return result
    } else {
        throw TmplNotMatchException(*tmpls)
    }
}


fun OpsContext.matched(vararg tmpls: Tmpl): Boolean {
    return lastMatchedTemplate in tmpls
}

fun OpsContext.matchedAny(): Boolean {
    return lastMatchedTemplate != null
}


private fun Double.formatDiff() = "%.6f".format(this)