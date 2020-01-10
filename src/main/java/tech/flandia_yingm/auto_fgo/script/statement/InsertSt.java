package tech.flandia_yingm.auto_fgo.script.statement;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import tech.flandia_yingm.auto_fgo.device.Device;
import tech.flandia_yingm.auto_fgo.script.Script;
import tech.flandia_yingm.auto_fgo.script.Statement;

@Slf4j
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class InsertSt extends Statement {

    private final String insertText;

    public InsertSt(String name, String insertText, int delay) {
        super(name, delay);
        this.insertText = insertText;
    }

    @Override
    protected void runStatement(Device device, Script superScript) {
        device.insert(insertText);
    }

}
