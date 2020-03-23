package tech.flandia_yingm.auto_fgo.device.android;

import lombok.experimental.UtilityClass;
import lombok.val;
import se.vidstige.jadb.JadbException;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

@UtilityClass
public class AndroidEmulatorUtils {

    public static void main(String[] args) throws IOException, JadbException {
        val ae = new AdbDevice("emulator-5554");
        writeImage(ae);
    }

    private static void writeImage(AdbDevice ae) throws IOException {
        ImageIO.write(ae.capture(), "png", new File("./screen.png"));
    }

}
