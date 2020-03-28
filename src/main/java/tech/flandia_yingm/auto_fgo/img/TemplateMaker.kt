package tech.flandia_yingm.auto_fgo.img

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.io.File
import javax.imageio.ImageIO

fun BufferedImage.include(image: BufferedImage): BufferedImage {
    require(this.width == image.width && this.height == image.height)
    val width = this.width
    val height = this.height
    val result = BufferedImage(width, height, TYPE_INT_ARGB)
    for (x in 0 until width) {
        for (y in 0 until height) {
            val tCol = Color(this.getRGB(x, y), true)
            val iCol = Color(image.getRGB(x, y), true)
            if (tCol == iCol) {
                result.setRGB(x, y, tCol.rgb)
            } else {
                result.setRGB(x, y, Color(0, 0, 0, 0).rgb)
            }
        }
    }
    return result
}

fun BufferedImage.exclude(image: BufferedImage): BufferedImage {
    require(this.width == image.width && this.height == image.height)
    val width = this.width
    val height = this.height
    val result = BufferedImage(width, height, TYPE_INT_ARGB)
    for (x in 0 until width) {
        for (y in 0 until height) {
            val tCol = Color(this.getRGB(x, y), true)
            val iCol = Color(image.getRGB(x, y), true)
            if (tCol != iCol) {
                result.setRGB(x, y, tCol.rgb)
            } else {
                result.setRGB(x, y, Color(0, 0, 0, 0).rgb)
            }
        }
    }
    return result
}

fun main() {
    var quit = false
    var image = ImageIO.read(File(readLine()!!.replace("\"", "")))
    do {
        when (readLine()) {
            "in" -> image = image.include(ImageIO.read(File(readLine()!!.replace("\"", ""))))
            "ex" -> image = image.exclude(ImageIO.read(File(readLine()!!.replace("\"", ""))))
            "quit" -> quit = true
            else -> TODO()
        }
    } while (!quit)
    ImageIO.write(image, "png", File("in_infrastructure.png"))
}