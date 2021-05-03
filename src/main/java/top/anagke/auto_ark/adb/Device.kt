package top.anagke.auto_ark.adb

import org.mozilla.universalchardet.UniversalDetector


fun Process.wait(): Unit = waitFor().let { }
fun Process.stdout(): ByteArray = inputStream.use { it.readAllBytes() }
fun Process.stdoutStr(): String = run {
    val ud = UniversalDetector()
    val stdout = stdout()
    ud.handleData(stdout)
    ud.dataEnd()
    String(stdout, charset(ud.detectedCharset ?: "GBK"))
}