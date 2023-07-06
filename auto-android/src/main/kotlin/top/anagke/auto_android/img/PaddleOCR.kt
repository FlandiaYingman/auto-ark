package top.anagke.auto_android.img

import top.anagke.auto_android.native.openProc
import top.anagke.auto_android.native.waitText
import top.anagke.auto_android.util.TempFiles
import top.anagke.auto_android.util.minutes
import top.anagke.auto_android.util.useSystemTempFile
import kotlin.Double
import kotlin.IllegalArgumentException
import kotlin.String
import kotlin.io.path.writeBytes
import kotlin.let


fun ocr(img: Img): String {
    val paddleOCRExe = "paddleocr"
    return TempFiles.useSystemTempFile("png") { temp ->
        temp.writeBytes(Img.encode(img))

        val stdout = openProc(paddleOCRExe, "--det=false", "--show_log=false", "--image_dir", temp.toString())
            .waitText(charset = charset("GBK"), timeout = 3.minutes)
            .stdout
        Result.parse(stdout)
    }.text
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
                ?.let { (textString, confidenceString) -> Result(textString, confidenceString.toDouble()) }
                ?: throw IllegalArgumentException("cannot parse $string to Result")
        }
    }
}