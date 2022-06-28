package top.anagke.auto_android.img

@kotlinx.serialization.Serializable
data class Pos(val x: Int, val y: Int) : Comparable<Pos> {
    override fun toString(): String = "($x, $y)"

    override fun compareTo(other: Pos): Int {
        return Comparator
            .comparingInt<Pos> { it.y }
            .thenComparingInt { it.x }
            .compare(this, other)
    }

    operator fun plus(that: Pos): Pos {
        return Pos(this.x + that.x, this.y + that.y)
    }

    operator fun minus(that: Pos): Pos {
        return Pos(this.x - that.x, this.y - that.y)
    }

}
