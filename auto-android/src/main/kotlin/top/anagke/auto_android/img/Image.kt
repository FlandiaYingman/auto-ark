package top.anagke.auto_android.img


import org.bytedeco.javacpp.Loader
import org.bytedeco.opencv.opencv_java
import org.opencv.core.*
import org.opencv.core.CvType.CV_8UC1
import org.opencv.highgui.HighGui
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgcodecs.Imgcodecs.IMREAD_UNCHANGED
import org.opencv.imgproc.Imgproc.*
import org.tinylog.kotlin.Logger
import top.anagke.auto_android.util.Rect
import top.anagke.auto_android.util.Size
import java.nio.ByteBuffer
import kotlin.math.roundToInt


object OpenCV {

    init {
        Loader.load(opencv_java::class.java)
    }

    fun init() {
    }

}

class Img
private constructor(val mat: Mat) {

    val size: Size = Size(mat.width(), mat.height())

    companion object {

        init {
            OpenCV.init()
        }

        fun decode(originalData: ByteArray): Img? {
            if (originalData.isEmpty()) return null
            return Img(Imgcodecs.imdecode(MatOfByte(*originalData), IMREAD_UNCHANGED))
        }

        fun decodeRaw(width: Int, height: Int, raw: ByteArray): Img {
            if (raw.isEmpty()) return Img(Mat(height, width, CvType.CV_8UC3, Scalar(0.0)))
            val native = ByteBuffer.allocateDirect(width * height * 4).apply {
                put(raw, 12, raw.size - 12)
            }
            val m = Mat(height, width, CvType.CV_8UC4, native).apply {
                cvtColor(this, this, COLOR_RGBA2BGR)
            }
            return Img(m)
        }

        fun encode(img: Img): ByteArray {
            return encode(img.mat)
        }

        fun encode(mat: Mat): ByteArray {
            val encode = MatOfByte().also { Imgcodecs.imencode(".png", mat, it) }
            return encode.toArray()
        }

    }


    fun match(tmpl: Img): Double {
        if (this.mat.elemSize() == 0L) return 1.0

        return try {
            val sceneImg = discardAlpha(this.mat)
            val matImg = discardAlpha(tmpl.mat)
            val mask = extractAlpha(tmpl.mat)
            val result = Mat().also {
                matchTemplate(sceneImg, matImg, it, TM_CCORR_NORMED, mask)
            }
            1.0 - result[0, 0][0]
        } catch (e: Exception) {
            Logger.warn(e, "Error in matching")
            1.0
        }
    }

    fun find(tmpl: Img): WPos {
        val sceneImg = discardAlpha(this.mat)
        val tmplImg = discardAlpha(tmpl.mat)
        val result = if (hasAlpha(tmpl.mat)) {
            val tmplMask = extractAlpha(tmpl.mat)
            Mat().also { matchTemplate(sceneImg, tmplImg, it, TM_CCORR_NORMED, tmplMask) }
        } else {
            Mat().also { matchTemplate(sceneImg, tmplImg, it, TM_CCORR_NORMED) }
        }
        val loc = Core.minMaxLoc(result)
        return loc.maxLoc.toPos().weight(1.0 - loc.maxVal)
    }

    fun crop(rect: Rect): Img {
        val rectMat = org.opencv.core.Rect(rect.pos.x, rect.pos.y, rect.size.width, rect.size.height)
        val crop = this.mat.submat(rectMat)
        return Img(crop)
    }

    fun canny(threshold1: Double = 255.0 / 3, threshold2: Double = 255.0): Img {
        val canny = Mat().also { Canny(mat, it, threshold1, threshold2) }
        return Img(canny)
    }

    fun blur(radius: Double): Img {
        val kernelSize = radius.toInt() * 4 + 1
        val canny = Mat().also {
            GaussianBlur(mat, it, Size(kernelSize.toDouble(), kernelSize.toDouble()), radius)
        }
        HighGui.imshow(null, canny)
        return Img(canny)
    }

    fun invert(): Img {
        val gray = Mat()
        cvtColor(this.mat, gray, COLOR_BGR2GRAY)
        Core.bitwise_not(gray, gray)

        return Img(gray)
    }

    fun show() {
        // Doing this because currently HighGui.imshow() doesn't support transparency.
        var mat = this.mat
        if (mat.channels() >= 3) {
            mat = Mat().also { cvtColor(mat, it, COLOR_BGRA2BGR) }
        }
        HighGui.imshow(null, mat)
        HighGui.waitKey()
    }

}

private fun Point.toPos(): Pos {
    return Pos(x.roundToInt(), y.roundToInt())
}


private fun hasAlpha(mat: Mat): Boolean {
    return mat.channels() == 4
}

private fun extractAlpha(mat: Mat): Mat {
    require(hasAlpha(mat))
    val channels = ArrayList<Mat>().also { Core.split(mat, it) }
    return channels[3]
}

private fun discardAlpha(mat: Mat): Mat {
    val channels = mutableListOf<Mat>().also { Core.split(mat, it) }
    val alphaChannel = Mat(mat.size(), CV_8UC1, Scalar(255.0))
    when (channels.size) {
        4 -> channels[3] = alphaChannel
        3 -> channels += alphaChannel
        1 -> return mat
        else -> TODO("${channels.size}")
    }
    val result = Mat().also { Core.merge(channels, it) }
    return result
}