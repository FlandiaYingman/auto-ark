package top.anagke.auto_android.img

import java.io.File

fun main() {
    File(".").listFiles()!!.forEach {
        if (it.extension == "png") {
            val img = Img.decode(it.readBytes())!!
            val edge = img.blur(1.0).canny(255.0 / 3, 255.0)
            it.resolveSibling("${it.nameWithoutExtension}_edge.${it.extension}").writeBytes(Img.encode(edge))
        }
    }
}