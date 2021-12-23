package top.anagke.auto_android.util

data class Rect(val pos: Pos, val size: Size) {
    constructor(x: Int, y: Int, width: Int, height: Int) : this(Pos(x, y), Size(width, height))

    fun center() = Pos(pos.x + size.width / 2, pos.y + size.height / 2)
}