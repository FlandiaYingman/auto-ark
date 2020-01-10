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

public interface Device {

    void tap(Point point);

    void swipe(Point start, Point end, long duration);

    void insert(String text);

    BufferedImage capture();


    default boolean match(Template template) {
        val log = LoggerFactory.getLogger(this.getClass());

        log.debug("{} - Matching template: {}", this, template);
        val similarity = Images.matchSameTemplate(capture(), template.getImage());
        val match = similarity > template.getThreshold();
        if (match) {
            log.debug("{} - Matched template: {}, similarity: {}", this, template, similarity);
        } else {
            log.debug("{} - Didn't matched template: {}, similarity: {}", this, template, similarity);
        }
        return match;
    }

    default boolean match(Template... templates) {
        return match(Arrays.asList(templates));
    }

    default boolean match(List<Template> templateList) {
        return templateList.stream()
                           .map(this::match)
                           .reduce(false, (a, b) -> a || b);
    }

    default Point find(Template template) {
        val log = LoggerFactory.getLogger(this.getClass());

        log.debug("{} - Finding template: {}", this, template);
        val similarityPoint = Images.matchTemplate(capture(), template.getImage());
        if (similarityPoint.getWeight() > template.getThreshold()) {
            return similarityPoint;
        } else {
            return Point.getEmpty();
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


    default void run(Script script) {
        script.runScript(this);
    }

}
