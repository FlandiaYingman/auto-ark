package tech.flandia_yingm.auto_fgo.fgo;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import tech.flandia_yingm.auto_fgo.ResourcesHelper;
import tech.flandia_yingm.auto_fgo.img.Template;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Slf4j
@UtilityClass
class FgoTemplates {

    private static BufferedImage getImage(String name) {
        try {
            log.debug("{} - Get image {} from resources", FgoTemplates.class.getSimpleName(), name);
            return ImageIO.read(ResourcesHelper.copyResourceToFile(FgoTemplates.class, name).toFile());
        } catch (IOException e) {
            log.error("{} - An I/O error occurs while getting image {}", FgoTemplates.class.getSimpleName(), name, e);
            return null;
        }
    }


    static final Template IDLE = new Template("idle", getImage("idle.png"), 0.95);

    static final Template INSTANCE = new Template("instance", getImage("instance.png"), 0.95);

    static final Template INSTANCE_FINISH = new Template("instance-finish", getImage("instance_finish.png"), 0.95);

    static final Template AP_INSUFFICIENT = new Template("ap-insufficient", getImage("ap_insufficient.png"), 0.95);

}
