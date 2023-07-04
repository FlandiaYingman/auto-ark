package top.anagke.auto_android.util

import top.anagke.auto_android.img.Pos
import kotlin.math.pow
import kotlin.math.sqrt

fun distance(pos1: Pos, pos2: Pos): Double {
    return sqrt((pos1.x - pos2.x).toDouble().pow(2) + (pos1.y - pos2.y).toDouble().pow(2))
}