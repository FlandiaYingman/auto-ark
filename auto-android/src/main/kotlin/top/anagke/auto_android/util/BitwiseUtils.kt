package top.anagke.auto_android.util

fun ByteArray.getUIntAt(idx: Int) =
    ((this[idx].toUInt() and 0xFFu) shl 0) or
            ((this[idx + 1].toUInt() and 0xFFu) shl 8) or
            ((this[idx + 2].toUInt() and 0xFFu) shl 16) or
            (this[idx + 3].toUInt() and 0xFFu shl 24)