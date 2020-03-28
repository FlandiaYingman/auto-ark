package tech.flandia_yingm.auto_fgo.arknights

import tech.flandia_yingm.auto_fgo.img.template

object ArknightsTemplates {

    val logoScreen = template("logo_screen", 0.99) {}
    val loggedInAnnouncement1 = template("logged_in_announcement_1") {}
    val loggedInAnnouncement2 = template("logged_in_announcement_2") {}
    val dailyBonus = template("daily_bonus") {}
    val dailyBonusConfirm = template("daily_bonus_confirm") {}

    val loggedIn = template("logged_in") {}

    val ableToStartMission = template("able_to_start_mission") {}
    val ableToConfirmMissionStart = template("able_to_confirm_mission_start") {}
    val missionSuccess = template("mission_success") {}
    val missionFailure = template("mission_failure") {}
    val levelUp = template("level_up") {}

    val requiredToUsePotion = template("able_to_use_potion", 0.99) {}
    val requiredToUseOriginium = template("able_to_use_originium") {}

    val productBubble = template("product_bubble", 0.94) {}
    val tradeBubble = template("trade_bubble") {}
    val tradeOrder = template("trade_order") {}
    val operatorWorkingWarning = template("operator_working_warning") {}


}