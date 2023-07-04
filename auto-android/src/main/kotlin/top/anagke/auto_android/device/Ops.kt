package top.anagke.auto_android.device

import org.tinylog.kotlin.Logger
import top.anagke.auto_android.img.Img
import top.anagke.auto_android.img.Tmpl
import top.anagke.auto_android.img.match
import top.anagke.auto_android.util.FrequencyLimiter
import top.anagke.auto_android.util.minutes
import top.anagke.auto_android.util.seconds
import java.time.Duration
import java.time.Instant
import kotlin.system.measureTimeMillis

class TimeoutException(message: String) : Exception(message)
class AssertException(message: String) : Exception(message)


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
            diff = screen.match(tmpl)
            matched = diff <= tmpl.threshold
        }
        Logger.debug("Matching $tmpl... result=$matched, difference=${diff.formatDiff()}, diffTime=$diffTime ms, capTime=$capTime ms")
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
    Logger.debug("Awaiting ${tmpls.contentToString()}...")
    val frequency = newFrequency(timeout)
    val begin = Instant.now()
    var tmpl: Tmpl?
    do {
        tmpl = frequency.run { which(*tmpls) }
        if (Duration.between(begin, Instant.now()).toMillis() > timeout) {
            throw TimeoutException("timeout after $timeout ms")
        }
    } while (tmpl == null)
    return tmpl
}

fun newFrequency(timeout: Long) = FrequencyLimiter(if (timeout <= 1.minutes) 1.seconds else 5.seconds)

fun Device.assert(vararg tmpls: Tmpl): Tmpl {
    Logger.debug("Asserting ${tmpls.toList()}...")
    val matched = which(*tmpls)
    if (matched != null) {
        return matched
    } else {
        throw AssertException("assert matching ${tmpls.contentToString()}")
    }
}


fun Device.whileMatch(vararg tmpls: Tmpl, timeout: Long = 1.minutes, block: () -> Unit) {
    val frequency = newFrequency(timeout)
    val begin = Instant.now()
    while (frequency.run { which(*tmpls) } != null) {
        block.invoke()
        if (Duration.between(begin, Instant.now()).toMillis() > timeout) {
            throw TimeoutException("timeout after $timeout ms")
        }
    }
}

fun Device.whileNotMatch(vararg tmpls: Tmpl, timeout: Long = 1.minutes, block: () -> Unit) {
    val frequency = newFrequency(timeout)
    val begin = Instant.now()
    while (frequency.run { which(*tmpls) } == null) {
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


fun delay(time: Int) {
    Thread.sleep(time.toLong())
}

fun delay(time: Long) {
    Thread.sleep(time)
}

@Suppress("unused")
fun Unit.delay(time: Int) {
    Thread.sleep(time.toLong())
}

fun Unit.nap() {
    delay(1000)
}

fun Unit.sleep() {
    delay(2000)
}

fun Unit.sleepl() {
    delay(5000)
}

fun sleep() {
    delay(2000)
}


private fun Double.formatDiff() = "%.6f".format(this)


