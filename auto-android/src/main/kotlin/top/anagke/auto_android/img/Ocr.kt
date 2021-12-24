package top.anagke.auto_android.img

import info.debatty.java.stringsimilarity.NormalizedLevenshtein
import mu.KotlinLogging
import top.anagke.auto_android.native.openProc
import top.anagke.auto_android.native.readText
import top.anagke.kio.util.TempFiles
import top.anagke.kio.util.useTempFile
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.copyTo
import kotlin.io.path.createDirectories
import kotlin.io.path.isRegularFile
import kotlin.io.path.relativeTo

private val logger = KotlinLogging.logger { }

private object Ocr {
    init {
        logger.info { "Extracting OCR resources..." }
        val resourceName = "/bin/tesseract"
        val resourceUri = Ocr.javaClass.getResource(resourceName)?.toURI()
            ?: throw FileNotFoundException("$resourceName not found")
        val resourcePath = Path.of(resourceUri)
        Files.walk(resourcePath)
            .toList()
            .filter { it.isRegularFile() }
            .forEach {
                logger.info { "Extracting $it..." }
                val dest = Path.of("bin/tesseract").resolve(it.relativeTo(resourcePath))
                dest.parent.createDirectories()
                it.copyTo(dest)
            }
    }

    fun init() {
    }
}


fun ocrTesseract(img: Img): String {
    return img.ocr()
}

fun Img.ocr(possibleWords: List<String> = emptyList()): String {
    Ocr.init()
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