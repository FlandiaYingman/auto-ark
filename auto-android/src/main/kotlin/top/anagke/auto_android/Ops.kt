@file:Suppress("unused")

package top.anagke.auto_android

import top.anagke.auto_android.img.Img
import top.anagke.auto_android.img.Tmpl
import top.anagke.auto_android.util.FrequencyLimiter
import top.anagke.auto_android.util.Pos
import top.anagke.auto_android.util.Rect
import top.anagke.auto_android.util.minutes
import top.anagke.auto_android.util.seconds
import java.time.Duration
import java.time.Instant
import kotlin.system.measureTimeMillis

class TimeoutException(message: String) : Exception(message)
class AssertException(message: String) : Exception(message)


private val log = mu.KotlinLogging.logger { }

private val lastMatchedTmplMap: MutableMap<Device, Tmpl?> = mutableMapOf()
private var Device.lastMatchedTmpl: Tmpl?
    get() {
        return lastMatchedTmplMap[this]
    }
    set(value) {
        lastMatchedTmplMap[this] = value
    }


fun Device.match(vararg tmpls: Tmpl): Boolean {
    return which(*tmpls) != null
}

fun Device.notMatch(vararg tmpls: Tmpl): Boolean {
    return which(*tmpls) == null
}

fun Device.which(vararg tmpls: Tmpl): Tmpl? {
    val screen: Img
    val capTime = measureTimeMillis { screen = cap() }
    for (tmpl in tmpls) {
        val diff: Double
        val matched: Boolean
        val diffTime = measureTimeMillis {
            diff = tmpl.diff(screen)
            matched = diff <= tmpl.threshold
        }
        log.debug { "Matching $tmpl... result=$matched, difference=${diff.formatDiff()}, diffTime=$diffTime ms, capTime=$capTime ms" }
        if (matched) {
            this.lastMatchedTmpl = tmpl
            return tmpl
        } else {
            this.lastMatchedTmpl = null
        }
    }
    return null
}


fun Device.await(vararg tmpls: Tmpl, timeout: Long = 1.minutes): Tmpl {
    log.debug { "Awaiting ${tmpls.contentToString()}..." }
    val frequencyLimiter = FrequencyLimiter(1.seconds)
    val begin = Instant.now()
    var tmpl: Tmpl?
    do {
        tmpl = frequencyLimiter.run { which(*tmpls) }
        if (Duration.between(begin, Instant.now()).toMillis() > timeout) {
            throw TimeoutException("timeout after $timeout ms")
        }
    } while (tmpl == null)
    return tmpl
}

fun Device.assert(vararg tmpls: Tmpl): Tmpl {
    log.debug { "Asserting ${tmpls.toList()}..." }
    val matched = which(*tmpls)
    if (matched != null) {
        return matched
    } else {
        throw AssertException("assert matching ${tmpls.contentToString()}")
    }
}


fun Device.whileMatch(vararg tmpls: Tmpl, timeout: Long = 1.minutes, block: () -> Unit) {
    val frequencyLimiter = FrequencyLimiter(1.seconds)
    val begin = Instant.now()
    while (frequencyLimiter.run { which(*tmpls) } != null) {
        block.invoke()
        if (Duration.between(begin, Instant.now()).toMillis() > timeout) {
            throw TimeoutException("timeout after $timeout ms")
        }
    }
}

fun Device.whileNotMatch(vararg tmpls: Tmpl, timeout: Long = 1.minutes, block: () -> Unit) {
    val frequencyLimiter = FrequencyLimiter(1.seconds)
    val begin = Instant.now()
    while (frequencyLimiter.run { which(*tmpls) } == null) {
        block.invoke()
        if (Duration.between(begin, Instant.now()).toMillis() > timeout) {
            throw TimeoutException("timeout after $timeout ms")
        }
    }
}


fun Device.matched(vararg tmpls: Tmpl): Boolean {
    if (tmpls.isEmpty()) return lastMatchedTmpl != null
    return lastMatchedTmpl in tmpls
}


fun Device.find(tmpl: Tmpl): Pos? {
    val screen = cap()
    val (pos, diff) = tmpl.find(screen)
    val result = if (diff <= tmpl.threshold) pos else null
    log.debug { "Finding $tmpl... result=$result, difference=${diff.formatDiff()}" }
    return result?.let { Rect(it, tmpl.img.size).center() }
}

fun Device.findEdge(tmpl: Tmpl): Pos? {
    val screen = cap()
    val (pos, diff) = tmpl.findEdge(screen)
    val result = if (diff <= tmpl.threshold) pos else null
    log.debug { "Finding edge $tmpl... result=$result, difference=${diff.formatDiff()}" }
    return result?.let { Rect(it, tmpl.img.size).center() }
}

fun Device.whileFind(tmpl: Tmpl, timeout: Long = 1.minutes, block: (Pos) -> Unit) {
    val frequencyLimiter = FrequencyLimiter(1.seconds)
    val begin = Instant.now()
    var pos: Pos?
    while (frequencyLimiter.run { find(tmpl) }.also { pos = it } != null) {
        block.invoke(pos!!)
        if (Duration.between(begin, Instant.now()).toMillis() > timeout) {
            throw TimeoutException("timeout after $timeout ms")
        }
    }
}

fun delay(time: Int) {
    Thread.sleep(time.toLong())
}

fun Unit.delay(time: Int) {
    Thread.sleep(time.toLong())
}

fun Unit.nap() {
    delay(1000)
}

fun Unit.sleep() {
    delay(2000)
}

fun sleep() {
    delay(2000)
}


private fun Double.formatDiff() = "%.6f".format(this)


fun Device.tapdListItem(itemIndex: Int, itemLength: Int, x: Int, y: Int, dragDy: Int, tapDy: Int) {
    if (itemIndex < itemLength) {
        tapd(x, y + ((itemIndex) * tapDy)).nap()
    } else {
        repeat(itemIndex - (itemLength - 1)) {
            drag(x, y, x, y - dragDy)
        }
        tapd(x, y + ((itemLength - 1) * tapDy)).nap()
    }
}