package top.anagke.auto_android.img

import info.debatty.java.stringsimilarity.JaroWinkler
import org.tinylog.kotlin.Logger

import top.anagke.auto_android.native.openProc
import top.anagke.auto_android.native.waitText
import top.anagke.auto_android.util.BinResources


fun ocrWord(img: Img, words: List<String> = emptyList()): String {
    BinResources.init()

    Logger.debug("OCR using initModule-built tesseract v5.0.1.20220118")
    val psm7 = rawOcr(img, 7)
    val psm8 = rawOcr(img, 8)
    val bestWord = words.minByOrNull {
        JaroWinkler().distance(it, psm7) + JaroWinkler().distance(it, psm8)
    } ?: psm7

    Logger.debug("OCR result: best word '$bestWord', stdout '$psm7', '$psm8'")
    return bestWord
}

private fun rawOcr(img: Img, psm: Int): String {
    val tesseractExec = "bin/tesseract/tesseract.exe"

    // Page Segmentation Modes:
    // ...
    // 7    Treat the image as a single text line.
    // 8    Treat the image as a single word.
    // ...
    val proc = openProc(tesseractExec, "stdin", "stdout", "-l", "chi_sim", "--psm", "$psm", "--dpi", "240")
    proc.outputStream.use { it.write(Img.encode(img)) }
    val stdout = proc.waitText(charset = Charsets.UTF_8).stdout.replace(Regex("\\s"), "")
    return stdout
}