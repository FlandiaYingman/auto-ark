package tech.flandia_yingm.auto_fgo.script.statement;

import lombok.ToString;
import lombok.val;
import tech.flandia_yingm.auto_fgo.device.Device;
import tech.flandia_yingm.auto_fgo.script.Script;
import tech.flandia_yingm.auto_fgo.script.Statement;
import tech.flandia_yingm.auto_fgo.img.Template;

@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class IfMatchSt extends Statement {

    private final Template template;

    private final Statement statement;

    public IfMatchSt(String name, Template template, Statement statement) {
        this(name, template, 0, statement);
    }

    public IfMatchSt(String name, Template template, int delay, Statement statement) {
        super(name, delay);
        this.template = template;
        this.statement = statement;
    }

    @Override
    protected void runStatement(Device device, Script superScript) {
        val script = Script.of(String.format("%s -> %s", superScript.getName(), this), statement);
        if (device.matches(template)) {
            device.run(script);
        }
    }

}
