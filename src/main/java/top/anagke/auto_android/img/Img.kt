package top.anagke.auto_android.img

import mu.KotlinLogging
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.core.Rect
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgcodecs.Imgcodecs.IMREAD_UNCHANGED
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.TM_CCORR_NORMED
import java.awt.Rectangle

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
            val imgMat = this.mat
            val tmplMat = tmpl.mat

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

    fun crop(rect: Rectangle): Img {
        val rectMat = Rect(rect.x, rect.y, rect.width, rect.height)
        val crop = this.mat.submat(rectMat)
        return Img(crop)
    }

    fun invert(): Img {
        val gray = Mat()
        Imgproc.cvtColor(this.mat, gray, Imgproc.COLOR_BGR2GRAY)
        Core.bitwise_not(gray, gray)

        return Img(gray)
    }

}

class Tmpl(val name: String, val threshold: Double, val tmplImgs: List<Img>) {

    fun diff(img: Img): Double {
        return tmplImgs.minOf { img.match(it) }
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


