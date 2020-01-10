package tech.flandia_yingm.auto_fgo.script;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import tech.flandia_yingm.auto_fgo.device.Device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Slf4j
@ToString(onlyExplicitlyIncluded = true)
public final class Script {

    @NonNull
    @ToString.Include
    private final String name;

    @NonNull
    private final List<Statement> statementList;

    private boolean terminated = false;


    private Script(String name, List<Statement> statementList) {
        this.name = name;
        this.statementList = statementList;
    }

    public static Script of(String name, List<Statement> statements) {
        return new Script(name, new ArrayList<>(statements));
    }

    public static Script of(String name, Statement... statements) {
        return new Script(name, Arrays.asList(statements));
    }


    public void runScript(Device device) {
        log.info("{} - Start running the script", this);
        for (Statement statement : statementList) {
            statement.run(device, this);
            if (terminated) {
                break;
            }
        }
        log.info("{} - Finish running the script", this);
    }

}
