package tech.flandia_yingm.auto_fgo.arknights

import org.junit.jupiter.api.Test
import tech.flandia_yingm.auto_fgo.device.android.AdbDevice

internal class ArknightsLoginAutoTest {

    @Test
    fun login() {
        val emu = AdbDevice("127.0.0.1:7555")
        val auto = ArknightsLoginAuto(emu)

        auto.login(ArknightsAccount("13046875179", "F1andiaYingM"))
    }
}