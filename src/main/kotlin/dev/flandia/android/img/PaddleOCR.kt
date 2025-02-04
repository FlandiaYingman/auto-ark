package dev.flandia.android.img

import dev.flandia.android.native.openProc
import dev.flandia.android.native.waitText
import dev.flandia.android.util.Rect
import dev.flandia.android.util.TempFiles
import dev.flandia.android.util.minutes
import dev.flandia.android.util.useSystemTempFile
import kotlin.Double
import kotlin.IllegalArgumentException
import kotlin.String
import kotlin.io.path.writeBytes
import kotlin.let
import kotlin.math.roundToInt

// PaddlePaddle and PaddleOCR are required.
// https://paddlepaddle.github.io/PaddleOCR/latest/quick_start.html

fun ocr(img: Img): String {
    val paddleOCRExe = "paddleocr"
    return TempFiles.useSystemTempFile("png") { temp ->
        temp.writeBytes(Img.encode(img))

        val stdout = openProc(paddleOCRExe, "--det=false", "--show_log=false", "--image_dir", temp.toString())
            .waitText(timeout = 10.minutes)
            .stdout
        Result.parse(stdout)
    }.text
}

fun det(img: Img): List<DetResult> {
    val paddleOCRExe = "paddleocr"
    return TempFiles.useSystemTempFile("png") { temp ->
        temp.writeBytes(Img.encode(img))

        val stdout = openProc(paddleOCRExe, "--det=true", "--show_log=false", "--image_dir", temp.toString())
            .waitText(timeout = 10.minutes)
            .stdout
        DetResult.parse(stdout)
    }
}

private data class Result(
    val text: String,
    val confidence: Double
) {
    companion object {
        fun parse(string: String): Result {
            val regex = """\('(.*?)', ([\d.]*?)\)""".toRegex()
            return regex.find(string)
                ?.destructured
                ?.let { (textString, confidenceString) -> Result(textString.trim {
                    Regex("[^\\p{IsHan}\\w-]").matches(it.toString())
                }, confidenceString.toDouble()) }
                ?: throw IllegalArgumentException("cannot parse $string to Result")
        }
    }
}

data class DetResult(
    val box: Rect,
    val text: String,
    val confidence: Double,
) {
    companion object {
        // [[[56.0, 386.0], [157.0, 386.0], [157.0, 420.0], [56.0, 420.0]], ('孔明明', 0.9967711567878723)]
        private val regex =
            """\[\[\[([\d.]*), ([\d.]*)], \[([\d.]*), ([\d.]*)], \[([\d.]*), ([\d.]*)], \[([\d.]*), ([\d.]*)]], \('(.*)',( [\d.]*)\)]""".toRegex()

        fun parse(str: String): List<DetResult> {
            return regex.findAll(str).map {
                val (
                    p1xStr,
                    p1yStr,
                    p2xStr,
                    p2yStr,
                    p3xStr,
                    p3yStr,
                    p4xStr,
                    p4yStr,
                    text,
                    confidenceStr
                ) = it.destructured
                val p1x = p1xStr.toDouble()
                val p1y = p1yStr.toDouble()
                val p2x = p2xStr.toDouble()
                val p2y = p2yStr.toDouble()
                val p3x = p3xStr.toDouble()
                val p3y = p3yStr.toDouble()
                val p4x = p4xStr.toDouble()
                val p4y = p4yStr.toDouble()
                val confidence = confidenceStr.toDouble()
                DetResult(
                    Rect(Pos(p1x.roundToInt(), p1y.roundToInt()), Pos(p3x.roundToInt(), p3y.roundToInt())),
                    text,
                    confidence
                )
            }.toList()
        }
    }
}