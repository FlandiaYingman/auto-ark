package top.anagke.auto_ark.img

import com.baidu.aip.ocr.AipOcr
import mu.KotlinLogging
import org.json.JSONException
import org.json.JSONObject
import top.anagke.auto_ark.dsl.Scheduler

private val log = KotlinLogging.logger { }
private val aipOcr = AipOcr("24091627", "tWdUbMYCgkZ6pAULERS8iBOL", "INUscWQAl7o3AGT8u8dBRpAzzWAyffus")


private const val QPS = 2
private const val MSPQ = 1000 / QPS
private val ocrScheduler = Scheduler(MSPQ.toLong())

@Synchronized
fun ocr(img: Img, retry: Int = 3): String = ocrScheduler.invoke {
    val json = aipOcr.basicGeneral(img.data, HashMap())
    try {
        val str = json.getJSONArray("words_result").joinToString("") { (it as JSONObject).getString("words") }
        log.debug { "OCR string $str" }
        str
    } catch (e: JSONException) {
        log.warn(e) { "Error occurs with $json" }
        if (retry > 0) ocr(img, retry - 1) else ""
    }
}