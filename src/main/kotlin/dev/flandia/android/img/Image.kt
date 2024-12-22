package dev.flandia.android.img


import org.bytedeco.javacpp.Loader
import org.bytedeco.opencv.opencv_java
import org.opencv.core.*
import org.opencv.core.CvType.CV_8UC1
import org.opencv.highgui.HighGui
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgcodecs.Imgcodecs.IMREAD_UNCHANGED
import org.opencv.imgproc.Imgproc.*
import org.tinylog.kotlin.Logger
import dev.flandia.android.util.Rect
import dev.flandia.android.util.Size
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
            dev.flandia.android.img.OpenCV.init()
        }

        fun empty(width: Int, height: Int): dev.flandia.android.img.Img {
            return dev.flandia.android.img.Img(Mat(height, width, CvType.CV_8UC3, Scalar(0.0)))
        }

        fun decode(originalData: ByteArray): dev.flandia.android.img.Img? {
            if (originalData.isEmpty()) return null
            return dev.flandia.android.img.Img(Imgcodecs.imdecode(MatOfByte(*originalData), IMREAD_UNCHANGED))
        }

        fun decodeRaw(width: Int, height: Int, raw: ByteArray): dev.flandia.android.img.Img {
            if (raw.isEmpty()) return dev.flandia.android.img.Img.Companion.empty(width, height)
            val native = ByteBuffer.allocateDirect(width * height * 4).apply {
                put(raw, 16, raw.size - 16)
            }
            val m = Mat(height, width, CvType.CV_8UC4, native).apply {
                cvtColor(this, this, COLOR_RGBA2BGR)
            }
            return dev.flandia.android.img.Img(m)
        }

        fun encode(img: dev.flandia.android.img.Img): ByteArray {
            return encode(img.mat)
        }

        fun encode(mat: Mat): ByteArray {
            val encode = MatOfByte().also { Imgcodecs.imencode(".png", mat, it) }
            return encode.toArray()
        }

    }


    fun match(tmpl: dev.flandia.android.img.Img): Double {
        if (this.mat.elemSize() == 0L) return 1.0

        return try {
            val sceneImg = dev.flandia.android.img.discardAlpha(this.mat)
            val matImg = dev.flandia.android.img.discardAlpha(tmpl.mat)
            val mask = dev.flandia.android.img.extractAlpha(tmpl.mat)
            val result = Mat().also {
                matchTemplate(sceneImg, matImg, it, TM_CCORR_NORMED, mask)
            }
            1.0 - result[0, 0][0]
        } catch (e: Exception) {
            Logger.warn(e, "Error in matching")
            1.0
        }
    }

    fun find(tmpl: dev.flandia.android.img.Img): dev.flandia.android.img.WPos {
        val sceneImg = dev.flandia.android.img.discardAlpha(this.mat)
        val tmplImg = dev.flandia.android.img.discardAlpha(tmpl.mat)
        val result = if (dev.flandia.android.img.hasAlpha(tmpl.mat)) {
            val tmplMask = dev.flandia.android.img.extractAlpha(tmpl.mat)
            Mat().also { matchTemplate(sceneImg, tmplImg, it, TM_CCORR_NORMED, tmplMask) }
        } else {
            Mat().also { matchTemplate(sceneImg, tmplImg, it, TM_CCORR_NORMED) }
        }
        val loc = Core.minMaxLoc(result)
        return loc.maxLoc.toPos().weight(1.0 - loc.maxVal)
    }

    fun crop(rect: Rect): dev.flandia.android.img.Img {
        val rectMat = org.opencv.core.Rect(rect.pos.x, rect.pos.y, rect.size.width, rect.size.height)
        val crop = this.mat.submat(rectMat)
        return dev.flandia.android.img.Img(crop)
    }

    fun canny(threshold1: Double = 255.0 / 3, threshold2: Double = 255.0): dev.flandia.android.img.Img {
        val canny = Mat().also { Canny(mat, it, threshold1, threshold2) }
        return dev.flandia.android.img.Img(canny)
    }

    fun blur(radius: Double): dev.flandia.android.img.Img {
        val kernelSize = radius.toInt() * 4 + 1
        val canny = Mat().also {
            GaussianBlur(mat, it, Size(kernelSize.toDouble(), kernelSize.toDouble()), radius)
        }
        HighGui.imshow(null, canny)
        return dev.flandia.android.img.Img(canny)
    }

    fun invert(): dev.flandia.android.img.Img {
        val gray = Mat()
        cvtColor(this.mat, gray, COLOR_BGR2GRAY)
        Core.bitwise_not(gray, gray)

        return dev.flandia.android.img.Img(gray)
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

    fun ocr(): String {
        return dev.flandia.android.img.ocr(this)
    }

    fun similarity(other: dev.flandia.android.img.Img): Double {
        val result = Mat().also {
            matchTemplate(this.mat, other.mat, it, TM_CCORR_NORMED)
        }
        return result[0, 0][0]
    }

}

private fun Point.toPos(): dev.flandia.android.img.Pos {
    return Pos(x.roundToInt(), y.roundToInt())
}


private fun hasAlpha(mat: Mat): Boolean {
    return mat.channels() == 4
}

private fun extractAlpha(mat: Mat): Mat {
    require(dev.flandia.android.img.hasAlpha(mat))
    val channels = ArrayList<Mat>().also { Core.split(mat, it) }
    return channels[3]
}

private fun discardAlpha(mat: Mat): Mat {
    val channels = mutableListOf<Mat>().also { Core.split(mat, it) }
    val alphaChannel = Mat(mat.size(), CV_8UC1, Scalar(255.0))
    when (channels.size) {
        4 -> channels.set(3, alphaChannel)
        3 -> channels += alphaChannel
        1 -> return mat
        else -> TODO("${channels.size}")
    }
    val result = Mat().also { Core.merge(channels, it) }
    return result
}