package tech.flandia_yingm.auto_fgo.device;

import lombok.val;
import org.slf4j.LoggerFactory;
import tech.flandia_yingm.auto_fgo.img.Images;
import tech.flandia_yingm.auto_fgo.img.Point;
import tech.flandia_yingm.auto_fgo.script.Script;
import tech.flandia_yingm.auto_fgo.img.Template;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.currentThread;

public interface Device {

    void tap(Point point);

    void swipe(Point start, Point end, long duration);

    void insert(String text);

    BufferedImage capture();


    default boolean matches(Template template) {
        val log = LoggerFactory.getLogger(this.getClass());

        log.debug("{} - Matching template: {}", this, template);
        val similarity = Images.matchTemplate(capture(), template.getImage());
        val match = similarity > template.getThreshold();
        if (match) {
            log.debug("{} - Matched template: {}, similarity: {}", this, template, similarity);
        } else {
            log.debug("{} - Didn't matched template: {}, similarity: {}", this, template, similarity);
        }
        return match;
    }

    default Template matches(Template... tmpls) {
        return matches(Arrays.asList(tmpls));
    }

    default Template matches(List<Template> tmpls) {
        val log = LoggerFactory.getLogger(this.getClass());
        log.debug("{} - Matching templates {}", this, tmpls);

        val img = capture();
        for (Template tmpl : tmpls) {
            val sim = Images.matchTemplate(img, tmpl.getImage());
            if (sim > tmpl.getThreshold()) {
                log.debug("{} - Matched template {}, similarity: {}", this, tmpl, sim);
                return tmpl;
            } else {
                log.debug("{} - Didn't match template {}, similarity: {}", this, tmpl, sim);
            }
        }
        return null;
    }

    default void tillMatched(Template tmpl) {
        while (!matches(tmpl) && !currentThread().isInterrupted()) {
            Thread.yield();
        }
    }

    default void tillMatched(Template... tmpls) {
        while (matches(tmpls) != null && !currentThread().isInterrupted()) {
            Thread.yield();
        }
    }

    default void delay(long ms) {
        val log = LoggerFactory.getLogger(this.getClass());
        try {
            log.debug("{} - Delaying {} ms", this, ms);
            Thread.sleep(ms);
            log.debug("{} - Delayed {} ms", this, ms);
        } catch (InterruptedException e) {
            //Ignored: thread interrupted
        }
    }

    default Point find(Template template) {
        val log = LoggerFactory.getLogger(this.getClass());

        log.debug("{} - Finding template: {}", this, template);
        val similarityPoint = Images.findTemplate(capture(), template.getImage());
        if (similarityPoint.getWeight() > template.getThreshold()) {
            return similarityPoint;
        } else {
            return Point.getEmpty();
        }
    }


    default void run(Script script) {
        script.runScript(this);
    }

}
