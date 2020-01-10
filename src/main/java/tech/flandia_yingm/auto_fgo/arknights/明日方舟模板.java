package tech.flandia_yingm.auto_fgo.arknights;

import lombok.experimental.UtilityClass;
import tech.flandia_yingm.auto_fgo.img.Template;
import tech.flandia_yingm.auto_fgo.img.Images;

import static tech.flandia_yingm.auto_fgo.img.Images.fromResource;
import static tech.flandia_yingm.auto_fgo.img.Template.of;


@UtilityClass
public class 明日方舟模板 {

    public static final Template LOGO_SCREEN = of("logo_screen", fromResource("Logo界面.png", ArknightsAuto.class), 0.95);

    public static final Template LOGON = of("logon", fromResource("已登录.png", ArknightsAuto.class), 0.95);


    public static final Template 任务开始 = of("任务开始", fromResource("任务开始.png", ArknightsAuto.class), 0.95);

    public static final Template 理智不足 = of("理智不足", fromResource("理智不足.png", ArknightsAuto.class), 0.99);

    public static final Template 理智不足且无合剂 = of("理智不足且无合剂", fromResource("理智不足且无合剂.png", ArknightsAuto.class), 0.95);

    public static final Template 任务开始确认 = of("任务开始确认", fromResource("任务开始确认.png", ArknightsAuto.class), 0.95);

    public static final Template 任务完成 = of("任务完成", fromResource("任务完成.png", ArknightsAuto.class), 0.95);

    public static final Template 任务失败 = of("任务失败", fromResource("任务失败.png", ArknightsAuto.class), 0.95);

    public static final Template 等级提升 = of("等级提升", fromResource("等级提升.png", ArknightsAuto.class), 0.95);


    public static final Template MANUFACTURING_STATION = of("manufacturing_station", fromResource("加工.png", ArknightsAuto.class), 0.95);

    public static final Template TRADE_STATION = of("trade_station", fromResource("贸易站气泡.png", ArknightsAuto.class), 0.95);

    public static final Template HAS_ORDER = of("has_order", fromResource("订单.png", ArknightsAuto.class), 0.95);

}
