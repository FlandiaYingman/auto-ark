package top.anagke.auto_android.img

import info.debatty.java.stringsimilarity.JaroWinkler
import info.debatty.java.stringsimilarity.NormalizedLevenshtein
import mu.KotlinLogging
import top.anagke.auto_android.native.openProc
import top.anagke.auto_android.native.waitText
import top.anagke.auto_android.util.BinResources
import top.anagke.kio.util.TempFiles
import top.anagke.kio.util.useTempFile
import java.nio.file.Files

private val logger = KotlinLogging.logger { }

fun ocrTesseract(img: Img): String {
    return img.ocr()
}

fun Img.ocr(possibleWords: List<String> = emptyList()): String {
    BinResources.init()
    logger.debug { "OCRing using prebuilt-tesseract-x86 $this" }

    var ocr = ""
    TempFiles.useTempFile(TempFiles.LOCAL_TEMP_DIR) {
        Files.write(it, Img.encode(this))

        val proc = openProc(
            "bin/tesseract/tesseract.exe",
            "$it", "-",
            "-l", "chi_sim",
            "--psm", "12",
            "--dpi", "300",
        )
        val output = proc.waitText().stdout

        ocr = output.trim().replace(Regex("\\s"), "")
        logger.debug { "Ocr RAW $this: '$ocr'" }
    }
    val result = possibleWords.maxByOrNull { NormalizedLevenshtein().similarity(it, ocr) } ?: ocr
    logger.debug { "Ocr RESULT $this: '$result'" }
    return result
}

fun ocrWord(img: Img, words: List<String> = emptyList()): String {
    BinResources.init()

    logger.debug { "OCR using initModule-built tesseract v5.0.1.20220118" }
    val psm7 = rawOcr(img, 7)
    val psm8 = rawOcr(img, 8)
    val bestWord = words.minByOrNull {
        JaroWinkler().distance(it, psm7) + JaroWinkler().distance(it, psm8)
    } ?: psm7

    logger.debug { "OCR result: best word '$bestWord', stdout '$psm7', '$psm8'" }
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
    val stdout = proc.waitText().stdout.replace(Regex("\\s"), "")
    return stdout
}