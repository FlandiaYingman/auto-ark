package tech.flandia_yingm.auto_fgo.fgo;

import lombok.experimental.UtilityClass;
import se.vidstige.jadb.JadbException;
import tech.flandia_yingm.auto_fgo.device.Device;
import tech.flandia_yingm.auto_fgo.device.android.AndroidEmulator;

import java.io.IOException;
import java.util.EnumSet;
import java.util.function.Consumer;

import static tech.flandia_yingm.auto_fgo.fgo.AutoFgo.State;

@UtilityClass
public class FgoScripts {


    public static void main(String[] args) throws IOException, JadbException {
        Device device = new AndroidEmulator("localhost:5555");
        AutoFgo autoFgo = new AutoFgo(device);
        for (int i = 0; i < 256; i++) {
            getDailyFireScript().accept(autoFgo);
        }
    }

    public Consumer<AutoFgo> getDailyFireScript() {
        return a -> {
            a.waitTillMatch(EnumSet.of(State.IDLE));
            a.enterInstance();
            a.chooseSupport();
            a.startInstance();

            a.useSkill(3, 3);
            a.useMasterSkill(1, 3);
            a.attack(8, 4, 5);

            a.useSkill(1, 2);
            a.useSkill(1, 3);
            a.useSkill(2, 2);
            a.attack(7, 3, 4);

            a.useSkill(1, 1, 3);
            a.attack(8, 4, 5);

            a.exitInstance();
        };
    }

}
