package top.anagke.auto_android.img

import mu.KotlinLogging
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Size
import org.opencv.highgui.HighGui
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgcodecs.Imgcodecs.IMREAD_UNCHANGED
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.*
import top.anagke.auto_android.util.Pos
import java.awt.FlowLayout
import java.awt.Rectangle
import java.io.File
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import kotlin.math.roundToInt


private val log = KotlinLogging.logger {}


object OpenCV {

    init {
        nu.pattern.OpenCV.loadLocally()
    }

    fun init() {
    }

}

class Img
private constructor(private val mat: Mat) {

    companion object {

        init {
            OpenCV.init()
        }

        fun decode(originalData: ByteArray): Img? {
            if (originalData.isEmpty()) return null
            return Img(Imgcodecs.imdecode(MatOfByte(*originalData), IMREAD_UNCHANGED))
        }

        fun encode(img: Img): ByteArray {
            val encode = MatOfByte().also { Imgcodecs.imencode(".png", img.mat, it) }
            return encode.toArray()
        }

    }


    fun match(tmpl: Img): Double {
        if (this.mat.elemSize() == 0L) return 1.0

        try {
            val result = Mat().also { matchTemplate(this.mat, tmpl.mat, it, TM_CCORR_NORMED, extractAlpha(tmpl.mat)) }
            return 1.0 - result[0, 0][0]
        } catch (e: Exception) {
            log.warn(e) { "Error in matching" }
            return 1.0
        }
    }

    fun find(tmpl: Img): Pair<Pos, Double> {
        val result = if (hasAlpha(tmpl.mat)) {
            Mat().also { matchTemplate(this.mat, tmpl.mat, it, TM_CCORR_NORMED, extractAlpha(tmpl.mat)) }
        } else {
            Mat().also { matchTemplate(this.mat, tmpl.mat, it, TM_CCORR_NORMED) }
        }
        val loc = Core.minMaxLoc(result)
        return loc.maxLoc.toPos() to (1.0 - loc.maxVal)
    }

    fun crop(rect: Rectangle): Img {
        val rectMat = Rect(rect.x, rect.y, rect.width, rect.height)
        val crop = this.mat.submat(rectMat)
        return Img(crop)
    }

    fun canny(threshold1: Double = 255.0 / 3, threshold2: Double = 255.0): Img {
        val canny = Mat().also { Canny(mat, it, threshold1, threshold2) }
        HighGui.imshow(null, canny)
        return Img(canny)
    }

    fun blur(radius: Double): Img {
        val kernelSize = radius.toInt() * 4 + 1
        val canny = Mat().also { GaussianBlur(mat, it, Size(kernelSize.toDouble(), kernelSize.toDouble()), radius) }
        HighGui.imshow(null, canny)
        return Img(canny)
    }

    fun invert(): Img {
        val gray = Mat()
        Imgproc.cvtColor(this.mat, gray, Imgproc.COLOR_BGR2GRAY)
        Core.bitwise_not(gray, gray)

        return Img(gray)
    }

    fun show() {
        val icon = ImageIcon(Img.encode(this))
        val frame = JFrame()
        frame.layout = FlowLayout()
        frame.setSize(200, 300)
        val lbl = JLabel()
        lbl.icon = icon
        frame.add(lbl)
        frame.isVisible = true
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    }

}

private fun Point.toPos(): Pos {
    return Pos(x.roundToInt(), y.roundToInt())
}

class Tmpl(val name: String, val threshold: Double, val tmplImgs: List<Img>) {

    fun diff(img: Img): Double {
        return tmplImgs.minOf { img.match(it) }
    }

    fun find(img: Img): Pair<Pos, Double> {
        return tmplImgs
            .map { img.blur(1.0).canny().find(it) }
            .minByOrNull { (pos, diff) -> diff }!!
    }

    override fun toString() = "Tmpl($name)"

}


private fun hasAlpha(mat: Mat): Boolean {
    val channels = ArrayList<Mat>()
    Core.split(mat, channels)

    return channels.size >= 3
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


fun main() {
    val capture = Img.decode(File("Screenshot_2021.11.12_23.20.11.656.png").readBytes())!!
}