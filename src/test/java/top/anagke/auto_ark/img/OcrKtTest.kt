package top.anagke.auto_ark.img

import org.junit.jupiter.api.Test

internal class OcrKtTest {

    @Test
    fun testOcr() {
        val testBytes = this.javaClass.getResourceAsStream("test.png")?.use { it.readBytes() }!!
        println(ocr(Img(testBytes)))
    }
}