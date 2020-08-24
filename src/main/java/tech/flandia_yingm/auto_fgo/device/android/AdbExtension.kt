package tech.flandia_yingm.auto_fgo.device.android

import se.vidstige.jadb.AdbFilterInputStream
import se.vidstige.jadb.JadbDevice
import se.vidstige.jadb.managers.Bash
import java.io.BufferedInputStream
import java.io.InputStream

internal fun JadbDevice.iGetTransport(): Any {
    val getTransport = JadbDevice::class.java.getDeclaredMethod("getTransport")
    getTransport.isAccessible = true

    return getTransport(this)
}

internal fun JadbDevice.iSend(transport: Any, command: String) {
    val transportClass = Class.forName("se.vidstige.jadb.Transport")
    val send = JadbDevice::class.java.getDeclaredMethod("send", transportClass, String::class.java)
    send.isAccessible = true

    send(this, transport, command)
}

internal fun Any.transportGetInputStream(): InputStream {
    val transportClass = Class.forName("se.vidstige.jadb.Transport")
    val getInputStream = transportClass.getDeclaredMethod("getInputStream")
    getInputStream.isAccessible = true
    return getInputStream(this) as InputStream
}

fun JadbDevice.executeShell(vararg commands: List<String>): InputStream {
    val transport = iGetTransport()
    val line = commands.joinToString(" & ") { command ->
        command[0] + command.slice(1 until command.size).joinToString(" ") { Bash.quote(it) }
    }
    println(line)

    iSend(transport, "shell:$line")
    return AdbFilterInputStream(BufferedInputStream(transport.transportGetInputStream()))
}