package tech.flandia_yingm.auto_fgo.script

import mu.KotlinLogging
import tech.flandia_yingm.auto_fgo.device.Device
import tech.flandia_yingm.auto_fgo.img.Point
import tech.flandia_yingm.auto_fgo.img.Template

open class Auto(private val device: Device) {

    companion object {
        val log = KotlinLogging.logger {}
    }

    fun tap(point: Point) {
        log.info { "$this - Tap $point" }
        device.tap(point)
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
        val result = device.matches(template)
        log.info { "$this - Matched $template, result: $result" }
        return result
    }

    fun assertMatching(template: Template) {
        if (!device.matches(template)) throw RuntimeException("not matching $template")
        log.info { "$this - Assert matching $template" }
    }

    fun waitMatching(template: Template) {
        log.info { "$this - Wait till $template is matched" }
        device.tillMatched(template)
    }

    fun waitMatching(vararg templates: Template): Template {
        log.info { "$this - Waiting till ${templates.toList()} is matched" }
        val result = device.tillMatched(*templates)
        log.info { "$this - Waited till ${templates.toList()} is matched, result: $result" }
        return result
    }

    fun findMatching(template: Template): Point {
        log.info { "$this - Finding $template" }
        val result = device.find(template)
        log.info { "$this - Found $template, result: $result" }
        return result
    }

    fun tapInWaitMatching(template: Template, point: Point) {
        while (!isMatching(template)) {
            tap(point)
            delay()
        }
    }

}