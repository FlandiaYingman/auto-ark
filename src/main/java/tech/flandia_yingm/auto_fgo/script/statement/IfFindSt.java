package tech.flandia_yingm.auto_fgo.script.statement;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import tech.flandia_yingm.auto_fgo.device.Device;
import tech.flandia_yingm.auto_fgo.script.Script;
import tech.flandia_yingm.auto_fgo.script.Statement;
import tech.flandia_yingm.auto_fgo.img.Template;
import tech.flandia_yingm.auto_fgo.img.Point;

@Slf4j
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class IfFindSt extends Statement {

    private final Template template;

    public IfFindSt( String name, Template template) {
        super(name);
        this.template = template;
    }

    @Override
    protected void runStatement(Device device, Script superScript) {
        Point weightPoint;
        if ((weightPoint = device.find(template)).isEmpty()) {
            device.tap(weightPoint);
        }
    }

}
