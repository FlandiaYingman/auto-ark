package tech.flandia_yingm.auto_fgo.img

import java.lang.Double.isNaN
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

class Point(
        val x: Int,
        val y: Int,
        val weight: Double = Double.NaN,
        val name: String = ""
) {

    val isValid: Boolean
        get() = x != -1 && y != -1 && !isNaN(weight)


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


    override fun toString(): String = if (!weight.isNaN()) "$name($x, $y, $weight)" else "$name($x, $y)"


    companion object {
        val EMPTY: Point
            get() = Point(-1, -1, Double.NaN)

        fun map(p: Point,
                xStartMin: Int, xStartMax: Int, xEndMin: Int, xEndMax: Int,
                yStartMin: Int, yStartMax: Int, yEndMin: Int, yEndMax: Int): Point {
            var x: Int = p.x
            var y: Int = p.y
            x = ((x - xStartMin).toDouble() / (xStartMax - xStartMin).toDouble() * (xEndMax - xEndMin).toDouble()).roundToInt()
            y = ((y - yStartMin).toDouble() / (yStartMax - yStartMin).toDouble() * (yEndMax - yEndMin).toDouble()).roundToInt()
            return Point(x, y)
        }
    }

}

infix fun Point.name(name: String): Point = Point(x, y, weight, name)

fun distance(p1: Point, p2: Point): Double {
    val distPoint = Point((p1 - p2).x.absoluteValue, (p1 - p2).y.absoluteValue)
    return sqrt(distPoint.x.toDouble().pow(2) + distPoint.y.toDouble().pow(2))
}