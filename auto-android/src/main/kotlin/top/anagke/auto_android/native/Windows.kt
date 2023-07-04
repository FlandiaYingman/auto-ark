package top.anagke.auto_android.native

import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg

object Windows : Platform {

    override fun getBlueStacksInstallDir(): String {
        return Advapi32Util.registryGetStringValue(
            WinReg.HKEY_LOCAL_MACHINE,
            """SOFTWARE\BlueStacks_nxt""",
            "InstallDir",
        )
    }

    override fun getBlueStacksDataDir(): String {
        return Advapi32Util.registryGetStringValue(
            WinReg.HKEY_LOCAL_MACHINE,
            """SOFTWARE\BlueStacks_nxt""",
            "UserDefinedDir",
        )
    }

}