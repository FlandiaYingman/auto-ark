package tech.flandia_yingm.auto_fgo.script.statement;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import tech.flandia_yingm.auto_fgo.device.Device;
import tech.flandia_yingm.auto_fgo.script.Script;
import tech.flandia_yingm.auto_fgo.script.Statement;

@Slf4j
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class TerminateSt extends Statement {

    public TerminateSt() {
        super("terminate");
    }

    @Override
    protected void runStatement(Device device, Script superScript) {
    }

    @Override
    public boolean isTerminated() {
        return true;
    }

    @Override
    protected void terminate() {
    }

}
