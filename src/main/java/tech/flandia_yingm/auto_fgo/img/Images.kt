package tech.flandia_yingm.auto_fgo.img

import mu.KotlinLogging
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import tech.flandia_yingm.auto_fgo.ResourcesHelper
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.util.*
import javax.imageio.ImageIO

object Images {

    private val log = KotlinLogging.logger {}

    init {
        loadOpenCvLibrary()
    }

    private fun loadOpenCvLibrary() {
        val libFile = when (val model = System.getProperty("sun.arch.data.model")) {
            "32" -> ResourcesHelper.copyResourceToFile(this.javaClass, "/opencv/x86/opencv_java401.dll")
            "64" -> ResourcesHelper.copyResourceToFile(this.javaClass, "/opencv/x64/opencv_java401.dll")
            else -> throw Exception("Unknown model $model")
        }
        System.load(libFile.toFile().absolutePath)
    }

    fun readResource(imageName: String, imageClass: Class<*>): BufferedImage {
        try {
            imageClass.getResourceAsStream(imageName).use {
                log.debug { "$this - Read the image resource $imageName in class $imageClass" }
                return ImageIO.read(it)
            }
        } catch (e: Exception) {
            log.error(e) { "$this - An error occurs while reading the image resource $imageName in class $imageClass" }
            return BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
        }
    }


    private fun toMat(img: BufferedImage): Mat {
        val copy = BufferedImage(img.width, img.height, BufferedImage.TYPE_4BYTE_ABGR)
        val copyGraphics = copy.createGraphics()
        copyGraphics.color = Color.BLACK
        copyGraphics.fillRect(0, 0, copy.width, copy.height)
        copyGraphics.drawImage(img, 0, 0, null)
        copyGraphics.dispose()
        val pixels = (copy.raster.dataBuffer as DataBufferByte).data
        val mat = Mat(img.height, img.width, CvType.CV_8UC4)
        mat.put(0, 0, pixels)
        return mat
    }

    private fun extractChannel(mat: Mat, nthChannel: Int): Mat {
        val splitMats = ArrayList<Mat>()
        Core.split(mat, splitMats)
        require(!(nthChannel < 0 || nthChannel >= splitMats.size)) {
            String.format(
                    "nthChannel %d < 0 || nthChannel %d >= splitedMats.size() %d", nthChannel, nthChannel, splitMats.size
            )
        }
        val resultMats = ArrayList<Mat>()
        for (i in splitMats.indices) {
            resultMats.add(splitMats[nthChannel])
        }
        //splitMats.forEach(Mat::release);
        val resultMat = Mat(mat.rows(), mat.cols(), mat.type())
        Core.merge(resultMats, resultMat)
        //resultMats.forEach(Mat::release);
        return resultMat
    }

    fun cropImage(image: BufferedImage, x: Int, y: Int, width: Int, height: Int): BufferedImage {
        return image.getSubimage(x, y, width, height)
    }

    @JvmStatic
    fun matchTemplate(image: BufferedImage, template: BufferedImage): Double {
        log.debug("{} - Matching the same template of the image", Images::class.java.simpleName)
        val imageMat = toMat(image)
        val templateMat = toMat(template)
        val maskMat = extractChannel(templateMat, 3)
        require(!(imageMat.rows() != templateMat.rows() || imageMat.cols() != templateMat.cols())) {
            String.format(
                    "imageMat.rows() %d != templateMat.rows() %d || imageMat.cols() %d != templateMat.cols() %d",
                    imageMat.rows(), templateMat.rows(), imageMat.cols(), templateMat.cols()
            )
        }
        val resMat = Mat(1, 1, CvType.CV_32FC1)
        Imgproc.matchTemplate(imageMat, templateMat, resMat, Imgproc.TM_CCORR_NORMED, maskMat)
        imageMat.release()
        templateMat.release()
        maskMat.release()
        val result = resMat[0, 0][0]
        log.debug("{} - Matched the same template of the image, result: {}", Images::class.java.simpleName, result)
        return result
    }

    @JvmStatic
    fun findTemplate(image: BufferedImage, template: BufferedImage): Point {
        log.debug("{} - Matching the template of the image", Images::class.java.simpleName)
        val imageMat = toMat(image)
        val templateMat = toMat(template)
        val maskMat = extractChannel(templateMat, 3)
        require(!(imageMat.rows() < templateMat.rows() || imageMat.cols() < templateMat.cols())) {
            String.format(
                    "imageMat.rows() %d < templateMat.rows() %d || imageMat.cols() %d < templateMat.cols() %d",
                    imageMat.rows(), templateMat.rows(), imageMat.cols(), templateMat.cols()
            )
        }
        require(!(templateMat.rows() != maskMat.rows() || templateMat.cols() != maskMat.cols())) {
            String.format(
                    "templateMat.rows() %d != maskMat.rows() %d || templateMat.cols() %d != maskMat.cols() %d",
                    templateMat.rows(), maskMat.rows(), templateMat.cols(), maskMat.cols()
            )
        }
        val resMat = Mat(imageMat.rows() - templateMat.rows() + 1, imageMat.cols() - templateMat.cols() + 1, CvType.CV_32FC1)
        Imgproc.matchTemplate(imageMat, templateMat, resMat, Imgproc.TM_CCORR_NORMED, maskMat)
        imageMat.release()
        templateMat.release()
        maskMat.release()
        val resLoc = Core.minMaxLoc(resMat)
        val result = Point(
                resLoc.maxLoc.x.toInt() + template.width / 2,
                resLoc.maxLoc.y.toInt() + template.height / 2,
                resLoc.maxVal
        )
        log.debug("{} - Matched the template of the image, result {}", Images::class.java.simpleName, result)
        return result
    }
}