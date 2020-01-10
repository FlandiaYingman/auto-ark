package tech.flandia_yingm.auto_fgo.script.statement;

import lombok.ToString;
import lombok.val;
import tech.flandia_yingm.auto_fgo.device.Device;
import tech.flandia_yingm.auto_fgo.script.Script;
import tech.flandia_yingm.auto_fgo.script.Statement;

import java.util.Arrays;
import java.util.List;

@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class SubSt extends Statement {

    private final List<Statement> statementList;

    public SubSt(String name, List<Statement> statementList) {
        super(name, 0);
        this.statementList = statementList;
    }

    public static SubSt of(String name, Statement... statements) {
        return new SubSt(name, Arrays.asList(statements));
    }

    @Override
    protected void runStatement(Device device, Script superScript) {
        val script = Script.of(String.format("%s -> %s", superScript.getName(), this), statementList);
        device.run(script);
    }

}
