package top.anagke.auto_android.device

import top.anagke.auto_android.device.TableSelector.ConstraintType.HORIZONTAL
import top.anagke.auto_android.device.TableSelector.ConstraintType.VERTICAL
import top.anagke.auto_android.img.Pos
import top.anagke.auto_android.util.Size

data class TableSelector(
    val origin: Pos,
    val finale: Pos,
    val itemInterval: Size,
    val dragInterval: Size,
    val viewWidth: Int,
    val viewHeight: Int,
    val tableWidth: Int,
    val tableHeight: Int,
    val tableConstraintType: ConstraintType? = null,
    var viewX: Int = 0,
    var viewY: Int = 0,
) {

    enum class ConstraintType {
        HORIZONTAL, VERTICAL,
    }

    fun fromSeqNum(itemSeqNum: Int): Pos {
        tableConstraintType!!

        val tableConstraint = when (tableConstraintType) {
            HORIZONTAL -> tableWidth
            VERTICAL -> tableHeight
        }
        val seq = itemSeqNum % tableConstraint
        val carry = itemSeqNum / tableConstraint
        return when (tableConstraintType) {
            HORIZONTAL -> Pos(seq, carry)
            VERTICAL -> Pos(carry, seq)
        }
    }

    fun Device.tapItem(item: Pos) {
        var itemX = item.x
        var itemY = item.y
        if (itemX >= viewWidth) {
            val swipeCount = itemX - (viewWidth - 1) - viewX
            viewX += swipeCount
            repeat(swipeCount) {
                dragv(origin.x, origin.y, -dragInterval.width, 0)
            }
            if (viewX + viewWidth == tableWidth) {
                swipev(origin.x, origin.y, -dragInterval.width, 0, 1.0)
            }
            itemX = viewWidth - 1
        }
        if (itemY >= viewHeight) {
            val swipeCount = itemY - (viewHeight - 1) - viewY
            viewY += swipeCount
            repeat(swipeCount) {
                dragv(origin.x, origin.y, 0, -dragInterval.height)
            }
            if (viewY + viewHeight == tableHeight) {
                swipev(origin.x, origin.y, 0, -dragInterval.height, 1.0)
            }
            itemY = viewHeight - 1
        }
        val xOffset = if (viewX + viewWidth == tableWidth && itemX == viewWidth - 1) {
            finale.x
        } else {
            origin.x + itemX * itemInterval.width
        }
        val yOffset = if (viewY + viewHeight == tableHeight && itemY == viewHeight - 1) {
            finale.y
        } else {
            origin.y + itemY * itemInterval.height
        }
        tap(xOffset, yOffset)
    }

    fun Device.resetTable() {
        swipev(origin.x, origin.y, dragInterval.width * tableWidth, dragInterval.height * tableHeight, 3.0)
    }

}

fun VerticalListSelector(
    origin: Pos,
    finale: Pos,
    itemInterval: Int,
    dragInterval: Int,
    viewHeight: Int,
    tableHeight: Int
) = TableSelector(
    origin,
    finale,
    itemInterval = Size(0, itemInterval),
    dragInterval = Size(0, dragInterval),
    viewWidth = 1,
    viewHeight,
    tableWidth = 1,
    tableHeight,
    tableConstraintType = HORIZONTAL
)