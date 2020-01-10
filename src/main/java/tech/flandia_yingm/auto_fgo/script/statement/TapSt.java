package tech.flandia_yingm.auto_fgo.script.statement;

import lombok.NonNull;
import lombok.ToString;
import tech.flandia_yingm.auto_fgo.device.Device;
import tech.flandia_yingm.auto_fgo.img.Point;
import tech.flandia_yingm.auto_fgo.script.Script;
import tech.flandia_yingm.auto_fgo.script.Statement;

@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class TapSt extends Statement {

    @NonNull
    @ToString.Include
    private final Point point;

    public TapSt(String name, Point point) {
        super(name);
        this.point = point;
    }

    public TapSt(String name, Point point, int delay) {
        super(name, delay);
        this.point = point;
    }

    @Override
    protected void runStatement(Device device, Script superScript) {
        device.tap(point);
    }

}
