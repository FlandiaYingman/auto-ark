package top.anagke.auto_android.device

import top.anagke.auto_android.util.OsMutex
import java.io.Closeable

class EmulatorHandle(
    val device: Device,
    private val mutex: OsMutex,
) : Closeable {
    override fun close() {
        mutex.close()
    }
}