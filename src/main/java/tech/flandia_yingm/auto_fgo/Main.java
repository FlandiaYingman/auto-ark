package tech.flandia_yingm.auto_fgo;

import lombok.val;
import se.vidstige.jadb.JadbException;
import tech.flandia_yingm.auto_fgo.arknights.ArknightsAccount;
import tech.flandia_yingm.auto_fgo.arknights.ArknightsAuto;
import tech.flandia_yingm.auto_fgo.device.android.AndroidEmulator;
import tech.flandia_yingm.auto_fgo.img.Point;
import tech.flandia_yingm.auto_fgo.script.Script;
import tech.flandia_yingm.auto_fgo.script.statement.TapSt;

import java.io.IOException;

public class Main {


    public static void main(String[] args) throws IOException, JadbException {
        val androidEmulator = new AndroidEmulator("emulator-5554");
        val autoAn = new ArknightsAuto(androidEmulator);
        while (true) {
            autoAn.代理指挥(ArknightsAuto.理智恢复策略.使用原石恢复);
        }
    }

}
