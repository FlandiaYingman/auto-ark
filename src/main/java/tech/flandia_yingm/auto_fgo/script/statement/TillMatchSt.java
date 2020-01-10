package tech.flandia_yingm.auto_fgo.script.statement;

import lombok.ToString;
import tech.flandia_yingm.auto_fgo.device.Device;
import tech.flandia_yingm.auto_fgo.script.Script;
import tech.flandia_yingm.auto_fgo.script.Statement;
import tech.flandia_yingm.auto_fgo.img.Template;

import java.util.Arrays;
import java.util.List;

@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class TillMatchSt extends Statement {

    private final List<Template> template;

    public TillMatchSt(String name, Template... template) {
        this(name, 0, template);
    }

    public TillMatchSt(String name, int delay, Template... template) {
        super(name, delay);
        this.template = Arrays.asList(template);
    }

    @Override
    protected void runStatement(Device device, Script superScript) {
        try {
            while (!device.match(template)) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
