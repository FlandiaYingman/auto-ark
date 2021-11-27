package top.anagke.auto_android.img

import mu.KotlinLogging
import top.anagke.auto_ark.native.openProc
import top.anagke.auto_ark.native.readText
import top.anagke.kio.util.TempFiles
import top.anagke.kio.util.useTempFile
import java.nio.file.Files

private val log = KotlinLogging.logger { }

fun ocrTesseract(img: Img): String {
    log.debug { "OCRing using prebuilt-tesseract-x86 $img" }

    var ocr = ""
    TempFiles.useTempFile(TempFiles.LOCAL_TEMP_DIR) {
        Files.write(it, Img.encode(img))

        val proc = openProc("bin/tesseract/tesseract.exe", "$it", "-", "-l", "chi_sim", "--dpi", "300")
        val output = proc.readText().stdout

        ocr = output.trim().replace(Regex("\\s"), "")
        log.debug { "Ocr $img: '$ocr'" }
    }
    return ocr
}

fun Img.ocrNum(): String {
    log.debug { "OCRing using prebuilt-tesseract-x86 $this" }

    var ocr = ""
    TempFiles.useTempFile(TempFiles.LOCAL_TEMP_DIR) {
        Files.write(it, Img.encode(this))

        val proc = openProc("bin/tesseract/tesseract.exe", "$it", "-", "--dpi", "300", "--psm", "13")
        val output = proc.readText().stdout

        ocr = output.trim().replace(Regex("\\s"), "")
        log.debug { "Ocr $this: '$ocr'" }
    }
    return ocr.filter { it.isDigit() }
}