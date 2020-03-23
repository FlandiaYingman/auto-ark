package tech.flandia_yingm.auto_fgo.img

import kotlin.math.roundToInt

class Point constructor(val name: String, val x: Int, val y: Int, val weight: Double = Double.NaN) {

    constructor(x: Int, y: Int) : this("", x, y)
    constructor(x: Int, y: Int, weight: Double) : this("", x, y, weight)

    val isEmpty: Boolean
        get() = x == -1 && y == -1 && java.lang.Double.isNaN(weight)

    val isValid: Boolean
        get() = !isEmpty

    operator fun plus(p: Point): Point {
        return Point(x + p.x, y + p.y)
    }

    operator fun minus(p: Point): Point {
        return Point(x - p.x, y - p.y)
    }

    operator fun times(n: Int): Point {
        return Point(x * n, y * n)
    }

    operator fun div(n: Int): Point {
        return Point(x / n, y / n)
    }


    fun addMultipliedOffset(offset: Point, n: Int): Point {
        return this + (offset * n)
    }

    override fun toString(): String = if (!weight.isNaN()) "$name($x, $y, $weight)" else "$name($x, $y)"

    companion object {
        @JvmStatic
        fun map(p: Point,
                xStartMin: Int, xStartMax: Int, xEndMin: Int, xEndMax: Int,
                yStartMin: Int, yStartMax: Int, yEndMin: Int, yEndMax: Int): Point {
            var x: Int = p.x
            var y: Int = p.y
            x = ((x - xStartMin).toDouble() / (xStartMax - xStartMin).toDouble() * (xEndMax - xEndMin).toDouble()).roundToInt()
            y = ((y - yStartMin).toDouble() / (yStartMax - yStartMin).toDouble() * (yEndMax - yEndMin).toDouble()).roundToInt()
            return Point(x, y)
        }

        @JvmStatic
        val EMPTY: Point
            get() = Point(-1, -1, Double.NaN)
    }

}