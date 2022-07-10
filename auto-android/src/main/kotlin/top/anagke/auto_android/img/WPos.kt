package top.anagke.auto_android.img

@kotlinx.serialization.Serializable
data class WPos(val x: Int, val y: Int, val w: Double) : Comparable<WPos> {

    override fun toString(): String = "($x, $y, $w)"

    companion object {
        private val comparator = Comparator
            .comparingDouble<WPos> { it.w }
            .thenComparingInt { it.y }
            .thenComparingInt { it.x }
    }

    override fun compareTo(other: WPos) = comparator.compare(this, other)

    fun asPos() = Pos(x, y)

}
