package tech.flandia_yingm.auto_fgo.script.statement;

import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import tech.flandia_yingm.auto_fgo.device.Device;
import tech.flandia_yingm.auto_fgo.script.Script;
import tech.flandia_yingm.auto_fgo.script.Statement;
import tech.flandia_yingm.auto_fgo.img.Template;
import tech.flandia_yingm.auto_fgo.img.Point;

@Slf4j
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class WhileFindSt extends Statement {

    private final Template template;

    private final int frequency;

    public WhileFindSt(@NonNull String name, Template template, int frequency) {
        super(name);
        this.template = template;
        this.frequency = frequency;
    }

    @Override
    protected void runStatement(Device device, Script superScript) {
        var weightPoint = Point.getEmpty();
        while ((weightPoint = device.find(template)).isEmpty()) {
            device.tap(weightPoint);
            device.delay(frequency);
        }
    }

}
