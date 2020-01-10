package tech.flandia_yingm.auto_fgo.script.statement;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import tech.flandia_yingm.auto_fgo.device.Device;
import tech.flandia_yingm.auto_fgo.script.Script;
import tech.flandia_yingm.auto_fgo.script.Statement;
import tech.flandia_yingm.auto_fgo.img.Point;

@Slf4j
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class SwipeSt extends Statement {

    private final Point startPoint;

    private final Point stopPoint;

    private final int duration;

    public SwipeSt(String name, Point startPoint, Point stopPoint, int duration, int delay) {
        super(name, delay);
        this.startPoint = startPoint;
        this.stopPoint = stopPoint;
        this.duration = duration;
    }

    @Override
    protected void runStatement(Device device, Script superScript) {
        device.swipe(startPoint, stopPoint, duration);
    }

}
