package tech.flandia_yingm.auto_fgo.device.android;

import lombok.experimental.UtilityClass;
import lombok.val;
import se.vidstige.jadb.JadbException;

import java.io.IOException;

@UtilityClass
public class AndroidEmulatorUtils {

    public static void main(String[] args) throws IOException, JadbException {
        val ae = new AndroidEmulator("localhost:5555");
        ae.recordEvents();
    }

}
