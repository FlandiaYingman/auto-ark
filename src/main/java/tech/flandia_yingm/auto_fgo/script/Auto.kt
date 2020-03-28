package tech.flandia_yingm.auto_fgo.script

import mu.KotlinLogging
import tech.flandia_yingm.auto_fgo.device.Device
import tech.flandia_yingm.auto_fgo.img.Point
import tech.flandia_yingm.auto_fgo.img.Template
import tech.flandia_yingm.auto_fgo.img.distance

open class Auto(private val device: Device) {

    companion object {
        val log = KotlinLogging.logger {}
    }

    fun tap(point: Point) {
        log.info { "$this - Tap $point" }
        device.tap(point)
    }

    fun slide(start: Point, end: Point, speed: Double = 0.075) {
        log.info { "$this - Slide $start to $end in $speed pixel/ms" }
        val distance = distance(start, end)
        device.swipe(start, end, (distance / speed).toLong())
    }

    fun delay(time: Long = 1000) {
        log.info { "$this - Delay $time" }
        device.delay(time)
    }

    fun input(text: String) {
        log.info { "$this - Input $text" }
        text.forEach {
            device.insert("$it")
            delay(25)
        }
    }

    fun isMatching(template: Template): Boolean {
        log.info { "$this - Matching $template" }
        val result = device.isMatching(template)
        log.info { "$this - Matched $template, result: $result" }
        return result
    }

    fun isMatching(vararg template: Template): Template {
        log.info { "$this - Matching ${listOf(*template)}" }
        val result = device.isMatching(*template)
        log.info { "$this - Matched ${listOf(*template)}, result: $result" }
        return result
    }

    fun assertMatching(template: Template) {
        if (!device.isMatching(template)) throw RuntimeException("not matching $template")
        log.info { "$this - Assert matching $template" }
    }

    fun assertMatching(vararg template: Template) {
        if (device.isMatching(*template) == Template.EMPTY_TEMPLATE) throw RuntimeException("not matching ${listOf(*template)}")
        log.info { "$this - Assert matching ${listOf(*template)}" }
    }

    fun waitMatching(template: Template) {
        log.info { "$this - Wait till $template is matched" }
        device.waitMatching(template)
    }

    fun waitMatching(vararg templates: Template): Template {
        log.info { "$this - Waiting till ${listOf(*templates)} is matched" }
        val result = device.waitMatching(*templates)
        log.info { "$this - Waited till ${listOf(*templates)} is matched, result: $result" }
        return result
    }

    fun findMatching(template: Template): Point {
        log.info { "$this - Finding $template" }
        val result = device.findMatching(template)
        log.info { "$this - Found $template, result: $result" }
        return result
    }

}