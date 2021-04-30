package top.anagke.auto_ark.img

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.system.measureTimeMillis

internal class ImgKtTest {

    @Test
    fun testMatch() {
        OpenCV.init()
        val img = Img(File("test.png").readBytes())
        val tmpl = Img(File("test_tmpl.png").readBytes())
        val match = measureTimeMillis { println("match(img, tmpl) = ${"%.5f".format(match(img, tmpl))}") }
        println(match)
    }

}