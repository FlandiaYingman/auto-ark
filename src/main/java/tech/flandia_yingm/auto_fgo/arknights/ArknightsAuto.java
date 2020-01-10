package tech.flandia_yingm.auto_fgo.arknights;

import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import tech.flandia_yingm.auto_fgo.device.Device;

@Slf4j
@ToString(onlyExplicitlyIncluded = true)
public class ArknightsAuto {

    @NonNull
    @ToString.Include
    private final Device device;


    public ArknightsAuto(Device device) {
        this.device = device;
    }


    public void login(ArknightsAccount account) {
        log.info("{} - Logging in using account: {}", this, account);
        device.run(ArknightsScripts.getLogin(account));
        log.info("{} - Logon using account: {}", this, account);
    }

    public void commandAsAgent() {
        log.info("{} - Commanding as agent", this);
        device.run(ArknightsScripts.getCommandAsAgent());
        log.info("{} - Commanded successful as agent", this);
    }


    public void harvestManufacturingStation() {
        log.info("{} - Harvesting manufacturing station", this);
        device.run(ArknightsScripts.getHarvestManufacturingStation());
        log.info("{} - Harvested manufacturing station", this);
    }

    public void harvestTradeStation() {
        log.info("{} - Harvesting trade station", this);
        device.run(ArknightsScripts.getHarvestTradeStation());
        log.info("{} - Harvested trade station", this);
    }


    public void goLs5() {
        device.run(ArknightsScripts.getGoLs5());
    }

}
