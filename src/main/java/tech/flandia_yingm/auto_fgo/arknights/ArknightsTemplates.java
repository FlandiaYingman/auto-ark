package tech.flandia_yingm.auto_fgo.arknights;

import lombok.experimental.UtilityClass;
import tech.flandia_yingm.auto_fgo.img.Template;
import tech.flandia_yingm.auto_fgo.img.Images;


@UtilityClass
public class ArknightsTemplates {

    public final Template LOGO_SCREEN = Template.of("logo_screen",
            Images.readImageResource("logo_screen.png", ArknightsAuto.class), 0.95);

    public final Template LOGON = Template.of("logon",
            Images.readImageResource("logon.png", ArknightsAuto.class), 0.95);

    public final Template MISSION_START = Template.of("mission_start",
            Images.readImageResource("mission_start.png", ArknightsAuto.class), 0.95);

    public final Template MISSION_START_CONFIRM = Template.of("mission_start_confirm",
            Images.readImageResource("mission_start_confirm.png", ArknightsAuto.class), 0.95);

    public final Template MISSION_FINISH = Template.of("mission_finish",
            Images.readImageResource("mission_finish.png", ArknightsAuto.class), 0.95);

    public final Template MISSION_FAILED = Template.of("mission_finish",
            Images.readImageResource("mission_failed.png", ArknightsAuto.class), 0.95);

    public final Template LEVEL_UP = Template.of("mission_finish",
            Images.readImageResource("level_up.png", ArknightsAuto.class), 0.95);


    public final Template MANUFACTURING_STATION = Template.of("manufacturing_station",
            Images.readImageResource("manufacturing_station.png", ArknightsAuto.class), 0.95);

    public final Template TRADE_STATION = Template.of("trade_station",
            Images.readImageResource("trade_station.png", ArknightsAuto.class), 0.95);

    public final Template HAS_ORDER = Template.of("has_order",
            Images.readImageResource("has_order.png", ArknightsAuto.class), 0.95);

}
