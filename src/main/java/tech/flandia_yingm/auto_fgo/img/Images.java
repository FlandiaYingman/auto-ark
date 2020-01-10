package tech.flandia_yingm.auto_fgo.img;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

@Slf4j
@UtilityClass
public class Images {

    static {
        try {
            loadOpenCV_Lib();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadOpenCV_Lib() throws Exception {
        val model = System.getProperty("sun.arch.data.model");
        var libraryPath = "";
        if (model.equals("64")) {
            libraryPath = Paths.get("opencv/build/java/x64/").toAbsolutePath().toString();
        } else {
            libraryPath = Paths.get("opencv/build/java/x86/").toAbsolutePath().toString();
        }
        System.setProperty("java.library.path", libraryPath);

        val sysPath = ClassLoader.class.getDeclaredField("sys_paths");
        sysPath.setAccessible(true);
        sysPath.set(null, null);

        System.loadLibrary("opencv_java420");
    }

    private Mat toMat(BufferedImage img) {
        val copy = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        val copyGraphics = copy.createGraphics();
        copyGraphics.setColor(Color.BLACK);
        copyGraphics.fillRect(0, 0, copy.getWidth(), copy.getHeight());
        copyGraphics.drawImage(img, 0, 0, null);
        copyGraphics.dispose();

        val pixels = ((DataBufferByte) copy.getRaster().getDataBuffer()).getData();
        val mat = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC4);
        mat.put(0, 0, pixels);

        return mat;
    }

    private Mat extractChannel(Mat mat, int nthChannel) {
        val splitMats = new ArrayList<Mat>();
        Core.split(mat, splitMats);

        if (nthChannel < 0 || nthChannel >= splitMats.size()) {
            throw new IllegalArgumentException(String.format(
                    "nthChannel %d < 0 || nthChannel %d >= splitedMats.size() %d", nthChannel, nthChannel, splitMats.size()
            ));
        }

        val resultMats = new ArrayList<Mat>();
        for (int i = 0; i < splitMats.size(); i++) {
            resultMats.add(splitMats.get(nthChannel));
        }
        //splitMats.forEach(Mat::release);

        Mat resultMat = new Mat(mat.rows(), mat.cols(), mat.type());
        Core.merge(resultMats, resultMat);
        //resultMats.forEach(Mat::release);

        return resultMat;
    }


    public BufferedImage cropImage(BufferedImage image, int x, int y, int width, int height) {
        return image.getSubimage(x, y, width, height);
    }


    public double matchSameTemplate(BufferedImage image, BufferedImage template) {
        log.debug("{} - Matching the same template of the image", Images.class.getSimpleName());

        val imageMat = toMat(image);
        val templateMat = toMat(template);
        val maskMat = extractChannel(templateMat, 3);

        if (imageMat.rows() != templateMat.rows() || imageMat.cols() != templateMat.cols()) {
            throw new IllegalArgumentException(String.format(
                    "imageMat.rows() %d != templateMat.rows() %d || imageMat.cols() %d != templateMat.cols() %d",
                    imageMat.rows(), templateMat.rows(), imageMat.cols(), templateMat.cols()
            ));
        }
        val resMat = new Mat(1, 1, CvType.CV_32FC1);
        Imgproc.matchTemplate(imageMat, templateMat, resMat, Imgproc.TM_CCORR_NORMED, maskMat);
        imageMat.release();
        templateMat.release();
        maskMat.release();

        val result = resMat.get(0, 0)[0];

        log.debug("{} - Matched the same template of the image, result: {}", Images.class.getSimpleName(), result);
        return result;
    }

    public Point matchTemplate(BufferedImage image, BufferedImage template) {
        log.debug("{} - Matching the template of the image", Images.class.getSimpleName());
        val imageMat = toMat(image);
        val templateMat = toMat(template);
        val maskMat = extractChannel(templateMat, 3);

        if (imageMat.rows() < templateMat.rows() || imageMat.cols() < templateMat.cols()) {
            throw new IllegalArgumentException(String.format(
                    "imageMat.rows() %d < templateMat.rows() %d || imageMat.cols() %d < templateMat.cols() %d",
                    imageMat.rows(), templateMat.rows(), imageMat.cols(), templateMat.cols()
            ));
        }

        if (templateMat.rows() != maskMat.rows() || templateMat.cols() != maskMat.cols()) {
            throw new IllegalArgumentException(String.format(
                    "templateMat.rows() %d != maskMat.rows() %d || templateMat.cols() %d != maskMat.cols() %d",
                    templateMat.rows(), maskMat.rows(), templateMat.cols(), maskMat.cols()
            ));
        }

        val resMat = new Mat(imageMat.rows() - templateMat.rows() + 1, imageMat.cols() - templateMat.cols() + 1, CvType.CV_32FC1);

        Imgproc.matchTemplate(imageMat, templateMat, resMat, Imgproc.TM_CCORR_NORMED, maskMat);
        imageMat.release();
        templateMat.release();
        maskMat.release();

        val resLoc = Core.minMaxLoc(resMat);
        Point result = new Point(
                (int) resLoc.maxLoc.x + (template.getWidth() / 2),
                (int) resLoc.maxLoc.y + (template.getHeight() / 2),
                resLoc.maxVal
        );

        log.debug("{} - Matched the template of the image, result {}", Images.class.getSimpleName(), result);
        return result;
    }


    public BufferedImage readImageResource(String imageName, Class<?> imageClass) {
        try (val imageStream = imageClass.getResourceAsStream(imageName)) {
            log.debug("{} - Reading the image resource {} from class {}",
                    Images.class.getSimpleName(), imageName, imageClass);
            val read = ImageIO.read(imageStream);
            log.debug("{} - Read the image resource {} from class {}",
                    Images.class.getSimpleName(), imageName, imageClass);
            return read;
        } catch (IOException e) {
            log.error("{} - An I/O error occurs while reading the image resource {} from class {}",
                    Images.class.getSimpleName(), imageName, imageClass, e);
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }
    }

}
