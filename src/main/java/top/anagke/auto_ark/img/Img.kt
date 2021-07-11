package top.anagke.auto_ark.img

import mu.KotlinLogging
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgcodecs.Imgcodecs.IMREAD_UNCHANGED
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.TM_CCORR_NORMED
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.appConfig
import top.anagke.auto_ark.ark.autoRecruit
import java.awt.Rectangle
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.WindowConstants.DISPOSE_ON_CLOSE

private val log = KotlinLogging.logger {}


object OpenCV {

    init {
        nu.pattern.OpenCV.loadLocally()
    }

    fun init() {
    }

}

class Img(val data: ByteArray) {

    fun toMat(): Mat {
        OpenCV.init()
        return Imgcodecs.imdecode(MatOfByte(*data), IMREAD_UNCHANGED)
    }

    fun show() {
        try {
            val input = ByteArrayInputStream(data)
            val bufImage = ImageIO.read(input)
            val frame = JFrame()
            frame.defaultCloseOperation = DISPOSE_ON_CLOSE
            frame.contentPane.add(JLabel(ImageIcon(bufImage)))
            frame.pack()
            frame.isVisible = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

class Tmpl(val name: String, val threshold: Double, val tmplImgs: List<Img>) {

    fun diff(img: Img): Double {
        return tmplImgs.minOf { match(img, it) }
    }

    override fun toString() = "Tmpl($name)"

}


private fun extractAlpha(mat: Mat): Mat {
    val channels = ArrayList<Mat>()
    Core.split(mat, channels)

    require(channels.size >= 3) { "the mat is required to have alpha channel" }

    val resultMatChannels = List(channels.size) { channels[3] }
    val resultMat = Mat(mat.rows(), mat.cols(), mat.type())
    Core.merge(resultMatChannels, resultMat)
    try {
        return resultMat
    } finally {
        // channels.forEach { it.release() }
    }
}

fun match(img: Img, tmpl: Img): Double {
    if (img.data.isEmpty()) return 1.0

    try {
        val imgMat = img.toMat()
        val tmplMat = tmpl.toMat()

        val resizedImg = Mat().also { Imgproc.resize(imgMat, it, Size(), 0.5, 0.5) }
        val resizedTmpl = Mat().also { Imgproc.resize(tmplMat, it, Size(), 0.5, 0.5) }
        val resizedMask = extractAlpha(resizedTmpl)

        val result = Mat().also { Imgproc.matchTemplate(resizedImg, resizedTmpl, it, TM_CCORR_NORMED, resizedMask) }
        return 1.0 - result[0, 0][0]
    } catch (e: Exception) {
        log.warn(e) { "Error in matching" }
        return 1.0
    }
}


fun crop(img: Img, rect: Rectangle): Img {
    val imgMat = img.toMat()
    val rectMat = Rect(rect.x, rect.y, rect.width, rect.height)
    val crop = imgMat.submat(rectMat)
    val encode = MatOfByte().also { Imgcodecs.imencode(".png", crop, it) }
    try {
        return Img(encode.toArray())
    } finally {
//        imgMat.release()
//        crop.release()
//        encode.release()
    }
}

fun invert(img: Img): Img {
    val src = img.toMat()
    val gray = Mat()
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY)
    Core.bitwise_not(gray, gray)

    val encode = MatOfByte().also { Imgcodecs.imencode(".png", gray, it) }
    return Img(encode.toArray())
}


fun testTemplate(tmpl: Tmpl) {
    while (true) {
        val diff = tmpl.diff(Device().cap())
        println("'${tmpl.name}''s diff   = ${String.format("%.6f", diff)}")
        println("'${tmpl.name}''s result = ${diff < tmpl.threshold}")
        println("=".repeat(32))
    }
}
