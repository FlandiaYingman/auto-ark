package tech.flandia_yingm.auto_fgo.script;

import lombok.NonNull;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.flandia_yingm.auto_fgo.device.Device;

@ToString(onlyExplicitlyIncluded = true)
public abstract class Statement {

    @NonNull
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @NonNull
    @ToString.Include
    protected final String name;

    protected final int delay;

    private volatile boolean terminated = false;


    protected Statement(String name) {
        this(name, 0);
    }

    protected Statement(String name, int delay) {
        this.name = name;
        this.delay = delay;
    }


    public boolean isTerminated() {
        return terminated;
    }

    protected void terminate() {
        terminated = true;
    }


    public void run(Device device, Script superScript) {
        try {
            log.info("{} -> {}", superScript, this);
            runStatement(device, superScript);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected abstract void runStatement(Device device, Script superScript);

}
