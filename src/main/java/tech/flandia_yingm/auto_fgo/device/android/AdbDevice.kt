package tech.flandia_yingm.auto_fgo.device.android

import mu.KotlinLogging
import se.vidstige.jadb.JadbConnection
import se.vidstige.jadb.JadbDevice
import se.vidstige.jadb.JadbException
import se.vidstige.jadb.RemoteFile
import tech.flandia_yingm.auto_fgo.device.Device
import tech.flandia_yingm.auto_fgo.img.Point
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset.defaultCharset
import java.util.*
import java.util.concurrent.TimeUnit.SECONDS
import javax.imageio.ImageIO

class AdbDevice
private constructor(device: JadbDevice) : Device {

    companion object {
        private val log = KotlinLogging.logger {}

        fun connect(serial: String): AdbDevice {
            log.info { "$this - Connecting to ADB device, serial: $serial" }
            var failed = false
            do {
                runCatching {
                    log.info { "$this - Connected to ADB device, serial: $serial" }
                    AdbDevice(connect0(serial, failed))
                }.onSuccess {
                    return it
                }.onFailure {
                    log.debug(it) { "$this - An error occurs while connecting to ADB device $serial, retrying" }
                    failed = true
                }
            } while (true)
        }

        @Throws(IOException::class)
        private fun connect0(serial: String, doKillServer: Boolean): JadbDevice {
            if (doKillServer) {
                val adbKillProcess = Runtime.getRuntime().exec(arrayOf("adb", "kill-server"))
                adbKillProcess.waitFor(10, SECONDS)
            }

            val adbConnectProcess = Runtime.getRuntime().exec(arrayOf("adb", "connect", serial))
            adbConnectProcess.waitFor(10, SECONDS)

            val response = adbConnectProcess.inputStream.reader(defaultCharset()).use { it.readText() }
            if (response.contains("unable to connect to")) {
                throw JadbException(response)
            }

            val connection = JadbConnection()
            var optionalDevice: Optional<JadbDevice>
            do {
                optionalDevice = connection.devices.stream()
                        .filter { it.serial == serial }
                        .findAny()
            } while (optionalDevice.isEmpty)

            return optionalDevice.get()
        }
    }

    private val device: JadbDevice = device
    private val screenWidth: Int
    private val screenHeight: Int

    init {
        val capture = capture()
        screenWidth = capture.width
        screenHeight = capture.height
    }


    override fun tap(point: Point) {
        log.debug("{} - Touching the screen at point {}", this, point)
        device.executeShell("input", "tap", "${point.x}", "${point.y}").waitStream()
        log.debug("{} - Touched the screen at point {}", this, point)
    }

    override fun swipe(start: Point, end: Point, duration: Long) {
        log.debug("{} - Swiping the screen from point {} to point {}", this, start, end)
        device.executeShell("input", "touchscreen", "swipe", "${start.x}", "${start.y}", "${end.x}", "${end.y}", "$duration").waitStream()
        log.debug("{} - Swiped the screen from point {} to point {}", this, start, end)
    }

    override fun insert(text: String) {
        val newText = text.replace(" ".toRegex(), "%s")
        log.debug("{} - Inserting the text {}", this, newText)
        device.executeShell("input", "text", newText).waitStream()
        log.debug("{} - Inserted the text {}", this, newText)
    }

    override fun capture(): BufferedImage {
        log.debug("{} - Capturing the screen to the temp file: screen.png", this)
        device.executeShell("screencap", "-p", "/sdcard/screen.png").waitStream()
        device.pull(RemoteFile("/sdcard/screen.png"), File("screen.png"))
        log.debug("{} - Captured the screen to the temp file: screen.png", this)
        return ImageIO.read(File("screen.png"))
    }

}

@Throws(IOException::class)
private fun InputStream.waitStream() {
    this.readAllBytes()
    this.close()
}