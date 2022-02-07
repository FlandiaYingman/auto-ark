package top.anagke.auto_android.util

import net.jpountz.lz4.LZ4Factory

object LZ4 {
    val decompressor = LZ4Factory.fastestInstance().fastDecompressor()
}