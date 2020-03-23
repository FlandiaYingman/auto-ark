package tech.flandia_yingm.auto_fgo.arknights

import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.closeAnnouncement
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.confirmInput
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.confirmLogin
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.enterTrade
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.exitInfrastructure
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.loginAccount
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.manageAccount
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.passwordInput
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.screenCenter
import tech.flandia_yingm.auto_fgo.arknights.ArknightsPoints.usernameInput
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.loggedIn
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.loggedInAnnouncement
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.logoScreen
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.productBubble
import tech.flandia_yingm.auto_fgo.arknights.ArknightsTemplates.tradeOrder
import tech.flandia_yingm.auto_fgo.device.Device
import tech.flandia_yingm.auto_fgo.script.*

class ArknightsMissionAuto(
        device: Device,
        private val sanityPolicy: SanityPolicy = SanityPolicy.NEVER_USE
) : Auto(device) {

    enum class SanityPolicy {
        NEVER_USE, //永不
        USE_POTION, //使用合剂
        USE_ORIGINIUM; //使用源石

        fun canUsePotion(): Boolean = this == USE_POTION || this == USE_ORIGINIUM
        fun canUseOriginium(): Boolean = this == USE_ORIGINIUM
    }

    fun autoCommand() {
        operation {
            invoke whenAble startMission

            invoke ifAble (usePotion thenInvokeWhenAble startMission)
            invoke ifAble (useOriginium thenInvokeWhenAble startMission)

            invoke whenAble confirmMissionStart
            invoke whenAble finishMission
        }.invoke()
    }

    private val startMission = operation {
        tap(ArknightsPoints.startMission)
    } ableAfter {
        waitMatching(ArknightsTemplates.ableToStartMission)
    }

    private val usePotion = operation {
        tap(ArknightsPoints.confirmRefreshSanity)
    } ableIf {
        if (isMatching(ArknightsTemplates.ableToUsePotion)) {
            if (sanityPolicy.canUsePotion()) {
                true
            } else {
                exception("no sanity but cannot use potion")
            }
        } else {
            false
        }
    }

    private val useOriginium = operation {
        tap(ArknightsPoints.useOriginium)
        delay()
        tap(ArknightsPoints.confirmRefreshSanity)
    } ableIf {
        if (isMatching(ArknightsTemplates.ableToUseOriginium)) {
            if (sanityPolicy.canUseOriginium()) {
                true
            } else {
                exception("no sanity but cannot use originium")
            }
        } else {
            false
        }
    }

    private val confirmMissionStart = operation {
        tap(ArknightsPoints.confirmMissionStart)
    } ableAfter {
        waitMatching(ArknightsTemplates.ableToConfirmMissionStart)
    }

    private val finishMission = operation {
        tap(ArknightsPoints.finishMission)
    } ableAfter {
        waitMatching(ArknightsTemplates.missionSuccess, ArknightsTemplates.missionFailure, ArknightsTemplates.levelUp)
        delay(5000)
        invoke ifAble (confirmLevelUp thenInvokeWhenAble operation { delay(5000) })
    }

    private val confirmLevelUp = operation {
        tap(ArknightsPoints.confirmLevelUp)
    } ableIf {
        isMatching(ArknightsTemplates.levelUp)
    }

}

class ArknightsInfrastructureAuto(device: Device) : Auto(device) {

    fun enterInfrastructure() {
        assertMatching(loggedIn)

        tap(ArknightsPoints.enterInfrastructure)
        delay(5000)
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

    fun exitInfrastructure() {
        // assertMatching(inInfrastructure)
        // TODO: Create a template inInfrastructure

        tap(exitInfrastructure)
        waitMatching(loggedIn)
    }

}

class ArknightsLoginAuto(device: Device) : Auto(device) {

    fun skipLogo() {
        tapInWaitMatching(logoScreen, screenCenter)
    }

    fun login(account: ArknightsAccount) {
        assertMatching(logoScreen)

        tap(manageAccount)
        delay()
        tap(loginAccount)
        delay(2000)

        tap(usernameInput)
        delay(2000)
        input(account.username)
        delay()
        tap(confirmInput)
        delay()

        tap(passwordInput)
        delay(2000)
        input(account.password)
        delay()
        tap(confirmInput)
        delay()

        tap(confirmLogin)
        waitMatching(loggedIn, loggedInAnnouncement).also {
            if (it == loggedInAnnouncement) {
                tap(closeAnnouncement)
                waitMatching(loggedIn)
            }
        }

        delay(3000)
    }

}
