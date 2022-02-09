package top.anagke.auto_android.native

interface Platform {
    companion object {
        fun getPlatform(): Platform {
            when {
                com.sun.jna.Platform.isWindows() -> {
                    return Windows
                }
                else -> throw UnsupportedOperationException("osType = ${com.sun.jna.Platform.getOSType()}")
            }
        }
    }

    fun getBlueStacksInstallDir(): String
    fun getBlueStacksDataDir(): String
}