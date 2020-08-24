package tech.flandia_yingm.auto_fgo.arknights

import tech.flandia_yingm.auto_fgo.Properties
import tech.flandia_yingm.auto_fgo.arknights.ArknightsAuto.SanityPolicy.NEVER_USE
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.chooseFirstOperator
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.chooseOperatorXOffset
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.chooseOperatorYOffset
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.closeAnnouncement
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.confirmInput
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.confirmLevelUp
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.confirmLogin
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.confirmManageRoom
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.confirmWorkingWarning
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.enterManageRoom
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.enterTrade
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.exitInfrastructure
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.exitManageRoom
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.finishMission
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.loginAccount
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.manageAccount
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.manageFirstRoom
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.passwordInput
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.screenCenter
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.selectFirstRoom
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.slideRoomAcrossFloorEnd
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.slideRoomAcrossFloorStart
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.slideRoomEnd
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.slideRoomStart
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.startMission
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.usernameInput
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.ableToConfirmMissionStart
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.ableToStartMission
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.levelUp
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.loggedIn
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.loggedInAnnouncement1
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.loggedInAnnouncement2
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.logoScreen
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.missionFailure
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.missionSuccess
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.operatorWorkingWarning
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.productBubble
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.requiredToUseOriginium
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.requiredToUsePotion
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.tradeOrder
import tech.flandia_yingm.auto_fgo.device.Device
import tech.flandia_yingm.auto_fgo.device.android.AdbDevice
import tech.flandia_yingm.auto_fgo.img.Template.Companion.EMPTY_TEMPLATE
import tech.flandia_yingm.auto_fgo.script.Auto

class ArknightsAuto(device: Device) : Auto(device) {

    enum class SanityPolicy {
        NEVER_USE, //永不
        USE_POTION, //使用合剂
        USE_ORIGINIUM; //使用源石

        private fun potionUsable(): Boolean = this == USE_POTION || this == USE_ORIGINIUM
        private fun originiumUsable(): Boolean = this == USE_ORIGINIUM

        fun assertPotionUsable() = if (!potionUsable()) throw Exception("Can't use potion.") else Unit
        fun assertOriginiumUsable() = if (!originiumUsable()) throw Exception("Can't use potion.") else Unit
    }

    fun autoCommand(sanityPolicy: SanityPolicy = NEVER_USE) {
        assertMatching(ableToStartMission)

        tap(startMission)
        waitMatching(ableToConfirmMissionStart, requiredToUsePotion, requiredToUseOriginium).run {
            if (this == requiredToUseOriginium) {
                sanityPolicy.assertPotionUsable()

                tap(ArknightsPoints.chooseOriginium)
                delay()
            }
            if (this == requiredToUsePotion || this == requiredToUseOriginium) {
                sanityPolicy.assertPotionUsable()

                tap(ArknightsPoints.confirmUseSanityItem)
                delay()

                tap(startMission)
                waitMatching(ableToConfirmMissionStart)
            }
        }

        tap(ArknightsPoints.confirmStartMission)
        waitMatching(missionSuccess, missionFailure, levelUp).run {
            if (this == levelUp) {
                tap(confirmLevelUp)
                waitMatching(missionSuccess)
            }
        }

        tap(finishMission)
        delay()
        tap(finishMission)
        waitMatching(ableToStartMission)
    }


    //Infrastructure

    fun enterInfrastructure() {
        assertMatching(loggedIn)

        tap(ArknightsPoints.enterInfrastructure)
        delay(10000)
    }

    fun exitInfrastructure() {
        // assertMatching(inInfrastructure)
        // TODO: Create a template inInfrastructure

        tap(exitInfrastructure)
        waitMatching(loggedIn)
    }

    fun harvestProduct() {
        do {
            val bubble = findMatching(productBubble)
            if (bubble.isValid) {
                tap(bubble)
                delay(3000)
            }
        } while (bubble.isValid)
    }

    fun harvestTrade() {
        do {
            val bubble = findMatching(ArknightsTemplates.tradeBubble)
            if (bubble.isValid) {
                tap(bubble)
                delay()
                tap(enterTrade)
                delay()
                while (isMatching(tradeOrder)) {
                    tap(ArknightsPoints.harvestTradeOrder)
                    delay(3000)
                }
                tap(exitInfrastructure)
                delay()
                tap(exitInfrastructure)
                delay()
            }
        } while (bubble.isValid)
    }

    fun enterManageRoom() {
        // assertMatching(inInfrastructure)
        // TODO: Create a template inInfrastructure

        tap(enterManageRoom)
        delay(3000)
    }

    fun exitManageRoom() {
        tap(exitManageRoom)
        delay(3000)
    }

    fun exchangeAll() {
        enterManageRoom()

        //Dormitory
        repeat(4) {
            slideRoom(4)
            slideRoomAcrossFloor()
            exchangeOperators(5)
        }
        exitManageRoom()

        enterManageRoom()

        //Others
        exchangeOperators(5)
        slideRoom(1)
        exchangeOperators(2)
        slideRoomAcrossFloor()

        exchangeOperators(3)
        slideRoom(1)
        exchangeOperators(3)
        slideRoom(3)
        slideRoomAcrossFloor()

        exchangeOperators(3)
        slideRoom(1)
        exchangeOperators(3)
        slideRoom(3)
        exchangeOperators(1)
        slideRoomAcrossFloor()

        exchangeOperators(3)
        slideRoom(1)
        exchangeOperators(3)

        exitManageRoom()
    }

    private fun exchangeOperators(count: Int) {
        tap(manageFirstRoom)
        delay()

        for (i in 0 until count * 2) {
            chooseOperator(i)
        }
        delay()

        tap(confirmManageRoom)
        delay()

        if (isMatching(operatorWorkingWarning)) {
            tap(confirmWorkingWarning)
        }
        delay(2000)
    }

    private fun chooseOperator(num: Int) {
        val x = num / 2
        val y = num % 2

        val point = chooseFirstOperator + chooseOperatorXOffset * x + chooseOperatorYOffset * y
        tap(point)
    }

    fun slideRoom(count: Int) {
        repeat(count) {
            slide(slideRoomStart, slideRoomEnd)
        }
        delay(2000)
    }

    fun slideRoomAcrossFloor() {
        slide(slideRoomAcrossFloorStart, slideRoomAcrossFloorEnd)
        tap(selectFirstRoom)
        delay(2000)
    }


    fun skipLogo() {
        while (isMatching(logoScreen, loggedIn) == EMPTY_TEMPLATE) {
            tap(screenCenter)
            delay()
        }
    }

    fun login(account: ArknightsAccount) {
        assertMatching(logoScreen, loggedIn)
        if (isMatching(loggedIn)) {
            return
        }

        tap(manageAccount)
        delay()
        tap(loginAccount)
        delay(2000)

        tap(usernameInput)
        delay(3000)
        input(account.username)
        delay()
        tap(confirmInput)
        delay()

        tap(passwordInput)
        delay(3000)
        input(account.password)
        delay()
        tap(confirmInput)
        delay()

        tap(confirmLogin)
        repeat(2) {
            waitMatching(loggedIn, loggedInAnnouncement1, loggedInAnnouncement2).run {
                if (this == loggedInAnnouncement1 || this == loggedInAnnouncement2) {
                    tap(closeAnnouncement)
                    waitMatching(loggedIn)
                }
            }
        }

        delay(3000)
    }

}

fun ArknightsAuto.autoInfrastructure() {
    enterInfrastructure()
    harvestProduct()
    harvestTrade()
    enterManageRoom()
    exchangeAll()
    exitManageRoom()
}

fun main() {
    val auto = ArknightsAuto(AdbDevice.connect(Properties.adbSerial))
    auto.exchangeAll()
}
