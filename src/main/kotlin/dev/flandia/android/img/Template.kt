package dev.flandia.android.img

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import dev.flandia.android.util.Rect
import java.io.File
import kotlin.time.measureTime

data class Tmpl(
    val name: String,
    val threshold: Double,
    val imgs: List<dev.flandia.android.img.Img>,
    val type: TmplType = TmplType.REGULAR
) {

    val imagesOptimized: List<Pair<dev.flandia.android.img.Img, Rect>> = imgs
        .associateWith { it.findBoundingBox() }
        .map { (img, box) -> img.crop(box) to box }

    override fun toString() = "Tmpl($name)"

}

enum class TmplType {
    REGULAR,
    EDGE,
}

fun dev.flandia.android.img.Img.match(tmpl: Tmpl): Double {
    return tmpl.imagesOptimized.minOf { (image, rect) -> this.crop(rect).match(image) }
}

private fun dev.flandia.android.img.Img.findBoundingBox(): Rect {
    val alphaChannel = Mat().also { Core.extractChannel(this.mat, it, 3) }
    val nonZeroPoints = Mat().also { Core.findNonZero(alphaChannel, it) }
    return Imgproc.boundingRect(nonZeroPoints).let { Rect(it.x, it.y, it.width, it.height) }
}

fun main() {
    val decode = dev.flandia.android.img.Img.decode(File("主界面.png").readBytes())!!
    println(measureTime { decode.findBoundingBox() })
}