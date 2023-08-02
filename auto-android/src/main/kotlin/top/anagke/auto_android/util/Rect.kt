package top.anagke.auto_android.util

import top.anagke.auto_android.img.Pos

data class Rect(val pos: Pos, val size: Size) {
    constructor(pos1: Pos, pos2: Pos) : this(pos1, (pos2 - pos1).run { Size(x, y) })
    constructor(x: Int, y: Int, width: Int, height: Int) : this(Pos(x, y), Size(width, height))

    fun center() = Pos(pos.x + size.width / 2, pos.y + size.height / 2)
}