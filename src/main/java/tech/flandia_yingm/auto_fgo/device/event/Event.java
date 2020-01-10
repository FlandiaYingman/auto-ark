package tech.flandia_yingm.auto_fgo.device.event;

import lombok.Value;
import tech.flandia_yingm.auto_fgo.img.Point;

@Value
public class Event {

    public enum Trigger {
        ALWAYS,
        MATCHING
    }

    private final Point point;

    private final String image;

    private final Trigger trigger;

    private final int delay;


    @Override
    public String toString() {
        return String.format("%d %d %s %s %d", point.getX(), point.getY(), image, trigger.name(), delay);
    }

}
