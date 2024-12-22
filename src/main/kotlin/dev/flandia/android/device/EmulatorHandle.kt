package dev.flandia.android.device

import java.io.Closeable

class EmulatorHandle(
    val device: Device,
    private val mutex: AutoCloseable,
) : Closeable {
    override fun close() {
        mutex.close()
    }
}