package tech.flandia_yingm.auto_fgo.fgo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import tech.flandia_yingm.auto_fgo.device.Device;
import tech.flandia_yingm.auto_fgo.img.Template;

import java.util.EnumSet;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class AutoFgo {

    public enum State {
        IDLE(FgoTemplates.IDLE),
        INSTANCE(FgoTemplates.INSTANCE),
        INSTANCE_FINISH(FgoTemplates.INSTANCE_FINISH);

        private final Template template;

        State(Template template) {
            this.template = template;
        }

        public Template getTemplate() {
            return template;
        }
    }


    private final Device device;


    private void delay(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean match(EnumSet<State> states) {
        val result = device.matches(states.stream()
                                          .map(State::getTemplate)
                                          .collect(Collectors.toList()));
        log.info("{} - Matched states {}, result: {}", this, states, result);
        // TODO: 1/10/2020 Change return value, brc
        return false;
    }

    public void waitTillMatch(EnumSet<State> states) {
        log.info("{} - Wait till match states {}", this, states);
        while (!match(states)) {
            delay(3000);
        }
    }

    public void waitTillMismatch(EnumSet<State> states) {
        log.info("{} - Wait till mismatch states {}", this, states);
        while (match(states)) {
            delay(3000);
        }
    }

    public void enterInstance() {
        enterInstance(true);
    }

    public void enterInstance(boolean useAppleIfInsufficient) {
        device.tap(FgoPoints.ENTER_INSTANCE);
        if (useAppleIfInsufficient) {
            delay(500);
            if (device.matches(FgoTemplates.AP_INSUFFICIENT)) {
                device.tap(FgoPoints.USE_APPLE_N1);
                delay(500);
                device.tap(FgoPoints.USE_APPLE_N2);
            }
        }
        delay(3000);
    }

    public void chooseSupport() {
        device.tap(FgoPoints.FIRST_SUPPORT);
        delay(2000);
    }

    public void startInstance() {
        device.tap(FgoPoints.START_INSTANCE);
        waitTillMatch(EnumSet.of(State.INSTANCE));
        delay(2000);
    }

    public void useSkill(int servant, int skill) {
        useSkill(servant, skill, 0);
    }

    public void useSkill(int servant, int skill, int target) {
        device.tap(FgoPoints.SKILL_1_1.addMultipliedOffset(FgoPoints.SKILL_SERVANT_OFFSET, servant - 1)
                                      .addMultipliedOffset(FgoPoints.SKILL_SKILL_OFFSET, skill - 1));
        if (target != 0) {
            delay(500);
            device.tap(FgoPoints.SKILL_TARGET_1.addMultipliedOffset(FgoPoints.SKILL_TARGET_OFFSET, target - 1));
        }
        delay(3000);
    }

    public void useMasterSkill(int skill) {
        useMasterSkill(skill, 0);
    }

    public void useMasterSkill(int skill, int target) {
        device.tap(FgoPoints.MASTER_SKILL_N1);
        delay(500);
        device.tap(FgoPoints.MASTER_SKILL_N2_1.addMultipliedOffset(FgoPoints.MASTER_SKILL_N2_OFFSET, skill - 1));
        if (target != 0) {
            delay(500);
            device.tap(FgoPoints.SKILL_TARGET_1.addMultipliedOffset(FgoPoints.SKILL_TARGET_OFFSET, target - 1));
        }
        delay(3000);
    }

    public void attack(int... cards) {
        device.tap(FgoPoints.ATTACK);
        delay(1500);
        for (int card : cards) {
            if (card <= 5) {
                chooseCard(card);
            } else {
                chooseNoble(card - 5);
            }
        }
        waitTillMatch(EnumSet.of(State.INSTANCE, State.INSTANCE_FINISH));
    }

    private void chooseCard(int card) {
        device.tap(FgoPoints.CARD_1.addMultipliedOffset(FgoPoints.CARD_OFFSET, card - 1));
        delay(500);
    }

    private void chooseNoble(int card) {
        device.tap(FgoPoints.NOBLE_1.addMultipliedOffset(FgoPoints.CARD_OFFSET, card - 1));
        delay(500);
    }

    public void exitInstance() {
        while (!match(EnumSet.of(State.IDLE))) {
            device.tap(FgoPoints.EXIT_INSTANCE);
            delay(3000);
        }
    }

}
