package tech.flandia_yingm.auto_fgo.script.statement;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import tech.flandia_yingm.auto_fgo.device.Device;
import tech.flandia_yingm.auto_fgo.script.Script;
import tech.flandia_yingm.auto_fgo.script.Statement;
import tech.flandia_yingm.auto_fgo.img.Template;

@Slf4j
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class WhileMatchSt extends Statement {

    private final Template template;

    private final Statement statement;

    private final boolean notFlag;


    public WhileMatchSt(String name, Template template, Statement statement) {
        this(name, template, statement, false);
    }

    public WhileMatchSt(String name, Template template, Statement statement, boolean notFlag) {
        super(name);
        this.template = template;
        this.statement = statement;
        this.notFlag = notFlag;
    }


    @Override
    protected void runStatement(Device device, Script superScript) {
        val script = Script.of(String.format("%s -> %s", superScript.getName(), this), statement);
        while (!device.match(template)) {
            device.run(script);
        }
    }

    public WhileMatchSt not() {
        return new WhileMatchSt(name, template, statement, !notFlag);

    }

}
