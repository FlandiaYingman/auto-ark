package dev.flandia.android.device.operation

import org.tinylog.kotlin.Logger
import dev.flandia.android.device.Device
import dev.flandia.android.device.newFrequency
import dev.flandia.android.img.*
import dev.flandia.android.util.Rect
import dev.flandia.android.util.minutes
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeoutException

fun Device.find(tmpl: Tmpl): Pos? {
    val screen = cap()
    return tmpl.imgs
        .map { it to screen.find(it, tmpl.type) }
        .minBy { (img, pos) -> pos }
        .let { (img, pos) ->
            if (pos.w < tmpl.threshold) {
                Rect(pos.asPos(), img.size).center()
            } else {
                null
            }
        }.also {
            Logger.debug("Find $tmpl... result=$it")
        }
}

private fun Img.find(img: Img, type: TmplType): WPos {
    return when (type) {
        TmplType.REGULAR -> this.find(img)
        TmplType.EDGE -> this.canny().find(img.canny())
    }
}

fun Device.whileFind(tmpl: Tmpl, timeout: Long = 1.minutes, block: (Pos) -> Unit) {
    val frequency = newFrequency(timeout)
    val begin = Instant.now()
    var pos: Pos?
    while (frequency.run { find(tmpl) }.also { pos = it } != null) {
        block.invoke(pos!!)
        if (Duration.between(begin, Instant.now()).toMillis() > timeout) {
            throw TimeoutException("timeout after $timeout ms")
        }
    }
}
