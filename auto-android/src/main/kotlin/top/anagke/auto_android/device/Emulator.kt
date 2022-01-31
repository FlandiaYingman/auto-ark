package top.anagke.auto_android.device

/**
 * Emu represents an Android Emulator (currently, only on Windows OS).
 *
 * Emu doesn't contain information of a specific emulator instance.
 */
interface Emulator {
    fun launch(): EmulatorHandle
}

