package top.anagke.auto_ark.img

import mu.KotlinLogging
import top.anagke.auto_ark.native.openProc
import top.anagke.auto_ark.native.stderrLog
import top.anagke.auto_ark.native.stdoutStr
import top.anagke.kio.util.TempFiles
import top.anagke.kio.util.useTempFile
import java.nio.file.Files


private val log = KotlinLogging.logger { }

fun ocrTesseract(img: Img): String {
    log.debug { "OCRing using prebuilt-tesseract-x86 $img" }

    var ocr = ""
    TempFiles.useTempFile(TempFiles.LOCAL_TEMP_DIR) {
        Files.write(it, img.data)

        val proc = openProc("bin/tesseract/tesseract.exe", "$it", "-", "-l", "chi_sim", "--dpi", "300")
        val output = proc.stdoutStr()
        proc.stderrLog()

        ocr = output.trim().replace(Regex("\\s"), "")
        log.info { "Ocr: '$ocr'" }
    }
    return ocr
}