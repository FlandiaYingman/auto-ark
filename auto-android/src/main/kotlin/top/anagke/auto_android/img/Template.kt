package top.anagke.auto_android.img

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import top.anagke.auto_android.util.Rect
import java.io.File
import kotlin.time.measureTime

data class Tmpl(
    val name: String,
    val threshold: Double,
    val imgs: List<Img>,
    val type: TmplType = TmplType.REGULAR
) {

    val imagesOptimized: List<Pair<Img, Rect>> = imgs
        .associateWith { it.findBoundingBox() }
        .map { (img, box) -> img.crop(box) to box }

    override fun toString() = "Tmpl($name)"

}

enum class TmplType {
    REGULAR,
    EDGE,
}

fun Img.match(tmpl: Tmpl): Double {
    return tmpl.imagesOptimized.minOf { (image, rect) -> this.crop(rect).match(image) }
}

private fun Img.findBoundingBox(): Rect {
    val alphaChannel = Mat().also { Core.extractChannel(this.mat, it, 3) }
    val nonZeroPoints = Mat().also { Core.findNonZero(alphaChannel, it) }
    return Imgproc.boundingRect(nonZeroPoints).let { Rect(it.x, it.y, it.width, it.height) }
}

fun main() {
    val decode = Img.decode(File("主界面.png").readBytes())!!
    println(measureTime { decode.findBoundingBox() })
}