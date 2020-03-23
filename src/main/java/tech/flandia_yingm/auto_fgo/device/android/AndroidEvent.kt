package tech.flandia_yingm.auto_fgo.device.android

import java.util.*

data class AndroidEvent(
        val deviceName: String,
        val eventType: Int,
        val eventCode: Int,
        val eventValue: Long
)

private val EVENT_REGEX = "\\s*?(?<dev>/dev/input/event\\d):\\s*?(?<type>[0-9a-f]{4})\\s*?(?<code>[0-9a-f]){4}\\s*?(?<value>[0-9a-f]{8})\\s*?".toRegex()

fun parse(str: String): AndroidEvent {
    val res = EVENT_REGEX.matchEntire(str)
    if (res != null) {
        return AndroidEvent(
                res.groups["dev"]!!.value,
                res.groups["type"]!!.value.toInt(16),
                res.groups["code"]!!.value.toInt(16),
                res.groups["value"]!!.value.toLong(16))
    } else {
        throw IllegalArgumentException("No matches found in $str")
    }
}