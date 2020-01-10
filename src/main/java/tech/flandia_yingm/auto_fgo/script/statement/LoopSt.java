package tech.flandia_yingm.auto_fgo.script.statement;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import tech.flandia_yingm.auto_fgo.device.Device;
import tech.flandia_yingm.auto_fgo.script.Script;
import tech.flandia_yingm.auto_fgo.script.Statement;

import java.util.ArrayList;

@Slf4j
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class LoopSt extends Statement {

    private final int loopTimes;

    private final Statement loop;


    public LoopSt(int loopTimes, Statement loop, int delay) {
        super("loop", delay);
        this.loopTimes = loopTimes;
        this.loop = loop;
    }

    @Override
    protected void runStatement(Device device, Script superScript) {
        val statementList = new ArrayList<Statement>();
        for (int i = 0; i < loopTimes; i++) {
            statementList.add(loop);
        }
        val script = Script.of(String.format("%s -> %s", superScript.getName(), this), statementList);
        device.run(script);
    }

}
