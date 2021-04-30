package top.anagke.auto_ark.adb

import mu.KotlinLogging
import org.mozilla.universalchardet.UniversalDetector
import top.anagke.auto_ark.dsl.Scheduler
import top.anagke.auto_ark.img.Img

class Device {

    private val capScheduler = Scheduler(500)

    fun cap(): Img = capScheduler.invoke {
        Img(adbProc("exec-out", "screencap -p").stdout())
    }

    fun tap(x: Int, y: Int) {
        return adbProc("shell", "input", "tap", "$x", "$y").wait()
    }

    fun back() {
        return adbProc("shell", "input", "keyevent", "4").wait()
    }

    fun input(str: String) {
        return adbProc("shell", "input", "text", str).wait()
    }


    operator fun invoke(ops: Ops): Any {
        return ops.invoke(OpsContext(this))
    }

}

fun Process.wait() = waitFor().let { }
fun Process.stdout() = inputStream.use { it.readAllBytes() }
fun Process.stdoutStr() = run {
    val ud = UniversalDetector()
    val stdout = stdout()
    ud.handleData(stdout)
    ud.dataEnd()
    String(stdout, charset(ud.detectedCharset ?: "GBK"))
}

private val log = KotlinLogging.logger { }
fun String.logLines() = lines().forEach { if (it.isNotBlank()) log.info { "> $it" } }

fun proc(vararg command: String): Process {
    val procBuilder = ProcessBuilder(*command)
    return procBuilder.start()
}

fun adbProc(vararg command: String): Process {
    val procBuilder = ProcessBuilder(listOf(listOf("adb"), command.toList()).flatten())
    return procBuilder.start()
}