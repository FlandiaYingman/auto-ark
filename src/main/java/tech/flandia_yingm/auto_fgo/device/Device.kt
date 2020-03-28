package tech.flandia_yingm.auto_fgo.device

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tech.flandia_yingm.auto_fgo.img.Images.findTemplate
import tech.flandia_yingm.auto_fgo.img.Images.matchTemplate
import tech.flandia_yingm.auto_fgo.img.Point
import tech.flandia_yingm.auto_fgo.img.Point.Companion.EMPTY
import tech.flandia_yingm.auto_fgo.img.Template
import java.awt.image.BufferedImage

interface Device {

    private val log: Logger
        get() = LoggerFactory.getLogger(this.javaClass)


    fun tap(point: Point)

    fun swipe(start: Point, end: Point, duration: Long)

    fun insert(text: String)

    fun capture(): BufferedImage


    fun delay(ms: Long) {
        val log = LoggerFactory.getLogger(this.javaClass)
        try {
            log.debug("{} - Delaying {} ms", this, ms)
            Thread.sleep(ms)
            log.debug("{} - Delayed {} ms", this, ms)
        } catch (e: InterruptedException) {
            //Ignored: thread interrupted
        }
    }


    fun isMatching(template: Template): Boolean {
        log.debug("{} - Matching template: {}", this, template)
        val similarity = matchTemplate(capture(), template.image)
        val matching = similarity > template.threshold
        if (matching) {
            log.debug("{} - Matched template: {}, similarity: {}", this, template, similarity)
        } else {
            log.debug("{} - Didn't match template: {}, similarity: {}", this, template, similarity)
        }
        return matching
    }

    fun isMatching(vararg templates: Template): Template {
        return isMatching(listOf(*templates))
    }

    fun isMatching(templates: List<Template>): Template {
        log.debug("{} - Matching templates {}", this, templates)
        val image = capture()
        for (template in templates) {
            val similarity = matchTemplate(image, template.image)
            if (similarity > template.threshold) {
                log.debug("{} - Matched template {}, similarity: {}", this, template, similarity)
                return template
            } else {
                log.debug("{} - Didn't match template {}, similarity: {}", this, template, similarity)
            }
        }
        return Template.EMPTY_TEMPLATE
    }

    fun waitMatching(template: Template) {
        while (!Thread.interrupted()) {
            val matching = isMatching(template)
            if (matching) {
                return
            }
        }
    }

    fun waitMatching(vararg templates: Template): Template {
        return waitMatching(listOf(*templates))
    }

    fun waitMatching(templates: List<Template>): Template {
        while (!Thread.interrupted()) {
            val matching = isMatching(templates)
            if (matching != Template.EMPTY_TEMPLATE) {
                return matching
            }
        }
        throw InterruptedException()
    }


    fun findMatching(template: Template): Point {
        log.debug("{} - Finding the template: {}", this, template)
        val sp = findTemplate(capture(), template.image)
        log.debug("{} - Found the template, point: {}", this, sp)
        return if (sp.weight > template.threshold) sp else EMPTY
    }

}