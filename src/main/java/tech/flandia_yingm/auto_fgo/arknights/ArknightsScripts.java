package tech.flandia_yingm.auto_fgo.arknights;

import lombok.experimental.UtilityClass;
import tech.flandia_yingm.auto_fgo.img.Point;
import tech.flandia_yingm.auto_fgo.script.Script;
import tech.flandia_yingm.auto_fgo.script.statement.*;

import static tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.*;

@UtilityClass
public class ArknightsScripts {

    public Script getLogin(ArknightsAccount account) {
        return Script.of("login",
                         new TillMatchSt("wait_till_logo_screen", LOGO_SCREEN),
                         new TapSt("manage_account", new Point(692, 509), 1000),
                         new TapSt("login_account", new Point(310, 379), 2000),

                         new TapSt("type_username", new Point(388, 323), 1000),
                         new InsertSt("insert_username", account.getUsername(), 1000),
                         new TapSt("ok_username", new Point(877, 485), 1000),

                         new TapSt("type_password", new Point(391, 357), 1000),
                         new InsertSt("insert_password", account.getPassword(), 1000),
                         new TapSt("ok_password", new Point(877, 485), 1000),

                         new TapSt("confirm_login", new Point(478, 429)),
                         new TillMatchSt("wait_till_logon", LOGON)
        );
    }

    public Script getCommandAsAgent() {
        return Script.of("command_as_agent",
                         new TillMatchSt("till_mission_start", MISSION_START),
                         new TapSt("mission_start", new Point(808, 492)),
                         new TillMatchSt("till_mission_start_confirm", MISSION_START_CONFIRM),
                         new TapSt("mission_start_confirm", new Point(827, 387)),
                         new TillMatchSt("till_mission_finish", MISSION_FINISH, MISSION_FAILED, LEVEL_UP),
                         new IfMatchSt("if_level_up", LEVEL_UP,
                                       new TapSt("level_up", new Point(480, 269))),
                         new TillMatchSt("till_mission_finish", MISSION_FINISH, MISSION_FAILED, LEVEL_UP),
                         new TapSt("mission_finish", new Point(480, 269))
        );
    }


    public Script getHarvestManufacturingStation() {
        return Script.of("harvest_manufacturing_station",
                         new WhileFindSt("harvest_manufacturing_station", MANUFACTURING_STATION, 3000)
        );
    }

    public Script getHarvestTradeStation() {
        return Script.of("harvest_trade_station",
                         new IfFindSt("touch_trade_station", TRADE_STATION),
                         new TapSt("touch_noble_metal_trade", new Point(59, 457))
        );
    }


    public Script getGoLs5() {
        return Script.of("go-LS5",
                         new TapSt("1", new Point(20848, 9174), 1500),
                         new TapSt("2", new Point(5979, 30000), 1500),
                         new TapSt("3", new Point(4669, 15582), 1500),
                         new TapSt("4", new Point(24288, 8082), 1500));
    }

}
