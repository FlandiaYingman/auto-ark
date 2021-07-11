package top.anagke.auto_ark.adb

import top.anagke.auto_ark.img.Tmpl


private val log = mu.KotlinLogging.logger { }

private val lastMatchedTmplMap: MutableMap<Device, Tmpl?> = mutableMapOf()
private var Device.lastMatchedTmpl: Tmpl?
    get() {
        return lastMatchedTmplMap[this]
    }
    set(value) {
        lastMatchedTmplMap[this] = value
    }


fun Device.match(tmpl: Tmpl): Boolean {
    val diff = tmpl.diff(cap())
    val result = diff <= tmpl.threshold
    this.lastMatchedTmpl = if (result) tmpl else null
    log.debug { "Match, result=$result, diff=${diff.formatDiff()}, $tmpl" }
    return result
}

fun Device.notMatch(tmpl: Tmpl): Boolean {
    return match(tmpl).not()
}

fun Device.which(vararg tmpls: Tmpl): Tmpl? {
    val screen = cap()
    tmpls.forEach { tmpl ->
        val diff = tmpl.diff(screen)
        val result = diff <= tmpl.threshold
        this.lastMatchedTmpl = if (result) tmpl else null
        log.debug { "Match, result=$result, diff=${diff.formatDiff()}, $tmpl" }
        if (result) return tmpl
    }
    return null
}


fun Device.await(vararg tmpls: Tmpl): Tmpl {
    log.debug { "Await matching ${tmpls.toList()}" }
    do {
        val screen = cap()
        val result = tmpls.find { tmpl ->
            val diff = tmpl.diff(screen)
            val result = diff <= tmpl.threshold
            this.lastMatchedTmpl = if (result) tmpl else null
            log.debug { "Match, result=$result, diff=${diff.formatDiff()}, $tmpl" }
            result
        }
        if (result != null) {
            return result
        }
    } while (result == null)
    throw InterruptedException()
}

fun Device.assert(vararg tmpls: Tmpl): Tmpl {
    log.debug { "Assert matching ${tmpls.toList()}" }
    val screen = cap()
    val result = tmpls.find { tmpl ->
        val diff = tmpl.diff(screen)
        val result = diff <= tmpl.threshold
        this.lastMatchedTmpl = if (result) tmpl else null
        log.debug { "Match, result=$result, diff=${diff.formatDiff()}, $tmpl" }
        result
    }
    require(result != null) { "The screen is required to match ${tmpls.contentToString()}" }
    return result
}


fun Device.matched(vararg tmpls: Tmpl): Boolean {
    return lastMatchedTmpl in tmpls
}

fun Device.matchedAny(): Boolean {
    return lastMatchedTmpl != null
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