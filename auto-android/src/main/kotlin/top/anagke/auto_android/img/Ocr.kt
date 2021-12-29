package top.anagke.auto_android.img

import info.debatty.java.stringsimilarity.NormalizedLevenshtein
import mu.KotlinLogging
import top.anagke.auto_android.BinResources
import top.anagke.auto_android.native.openProc
import top.anagke.auto_android.native.readText
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
        val output = proc.readText().stdout

        ocr = output.trim().replace(Regex("\\s"), "")
        logger.debug { "Ocr RAW $this: '$ocr'" }
    }
    val result = possibleWords.maxByOrNull { NormalizedLevenshtein().similarity(it, ocr) } ?: ocr
    logger.debug { "Ocr RESULT $this: '$result'" }
    return result
}