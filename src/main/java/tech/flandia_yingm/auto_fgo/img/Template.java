package tech.flandia_yingm.auto_fgo.img;

import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

import java.awt.image.BufferedImage;

@Value
@ToString(onlyExplicitlyIncluded = true)
public class Template {

    @NonNull
    @ToString.Include
    private final String name;

    @NonNull
    private final BufferedImage image;

    private double threshold;


    public static Template of(String name, BufferedImage image, double threshold) {
        return new Template(name, image, threshold);
    }

}
