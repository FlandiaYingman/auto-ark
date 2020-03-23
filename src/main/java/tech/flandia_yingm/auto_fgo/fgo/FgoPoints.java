package tech.flandia_yingm.auto_fgo.fgo;

import lombok.experimental.UtilityClass;
import tech.flandia_yingm.auto_fgo.img.Point;

@UtilityClass
class FgoPoints {

    static final Point ENTER_INSTANCE = new Point(17868, 8283);
    static final Point FIRST_SUPPORT = new Point(3358, 12669);
    static final Point START_INSTANCE = new Point(30515, 30765);

    static final Point USE_APPLE_N1 = new Point(9488, 14866);
    static final Point USE_APPLE_N2 = new Point(21400, 25546);

    static final Point SKILL_TARGET_1 = new Point(8550, 20343);
    static final Point SKILL_TARGET_OFFSET = new Point(16512, 20343).minus(SKILL_TARGET_1);

    static final Point SKILL_1_1 = new Point(1638, 26396);
    static final Point SKILL_SKILL_OFFSET = new Point(4121, 26396).minus(SKILL_1_1);
    static final Point SKILL_SERVANT_OFFSET = new Point(9830, 26396).minus(SKILL_1_1);

    static final Point ATTACK_TARGET_1 = new Point(13440, 2002);
    static final Point ATTACK_TARGET_OFFSET = new Point(7398, 2002).minus(ATTACK_TARGET_1);

    static final Point MASTER_SKILL_N1 = new Point(30566, 14381);
    static final Point MASTER_SKILL_N2_1 = new Point(22963, 14381);
    static final Point MASTER_SKILL_N2_OFFSET = new Point(25446, 14381).minus(MASTER_SKILL_N2_1);

    static final Point ATTACK = new Point(29132, 27807);

    static final Point CARD_1 = new Point(3379, 22876);
    static final Point NOBLE_1 = new Point(9522, 11286);
    static final Point CARD_OFFSET = new Point(9591, 23361).minus(CARD_1);

    static final Point EXIT_INSTANCE = new Point(30233, 30264);

}
