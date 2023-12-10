package uk.ac.bournemouth.ap.battleships

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import org.example.student.battleshipgame.StudentShip
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid
import kotlin.math.floor


class PlaceShipView : GridViewBase {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {

    }

    override val colCount: Int get() = BattleshipGrid.DEFAULT_COLUMNS
    override val rowCount: Int get() = BattleshipGrid.DEFAULT_ROWS
    lateinit var newPlayerShip: StudentShip

    val shipSizes: IntArray = BattleshipGrid.DEFAULT_SHIP_SIZES
    var shipNumber = 0


    override fun onDraw(canvas: Canvas) {
        drawGrid(canvas)
        drawShips(canvas)
    }

    private val shipPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.LTGRAY
    }

    private fun drawShips(canvas: Canvas) {
        val gridLeft = offsetLeft + paddingLeft
        val gridTop = offsetTop + paddingTop
        val shipMargins = cellWidth * 0.1f

        for (ship in playerShipList) {
            val shipLeft = ship.left * cellWidth + gridLeft + shipMargins
            val shipTop = ship.top * cellWidth + gridTop + shipMargins
            val shipRight = (ship.right + 1) * cellWidth + gridLeft - shipMargins
            val shipBottom = (ship.bottom + 1) * cellWidth + gridTop - shipMargins
            canvas.drawRect(shipLeft, shipTop, shipRight, shipBottom, shipPaint)
        }
        invalidate()
    }


    private val myGestureDetector = GestureDetectorCompat(context, MyGestureListener())

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return myGestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(ev: MotionEvent): Boolean {
            return true
        }

        /** Gets the column and row numbers, checks there is still a ship size in the list,
         * then checks that it wont overlap another ship if its placed at that column and row.*/
        override fun onSingleTapConfirmed(ev: MotionEvent): Boolean {

            val column = floor((ev.x - gridLeft) / cellWidth).toInt()
            val row = floor((ev.y - gridTop) / cellWidth).toInt()

            if (shipNumber < shipSizes.size) {
                newPlayerShip = StudentShip(row, column, row + shipSizes[shipNumber] - 1, column)

                if (!playerShipList.any { it.overlaps(newPlayerShip) } && newPlayerShip.bottom < rowCount) {
                    shipNumber += 1
                    tempPlayerShipList.add(newPlayerShip)
                }
            }
            return true
        }

        /** Gets the column and row numbers, checks there is still a ship size in the list,
         * then checks that it wont overlap another ship if its placed at that column and row.*/
        override fun onDoubleTap(ev: MotionEvent): Boolean {

            val column = floor((ev.x - gridLeft) / cellWidth).toInt()
            val row = floor((ev.y - gridTop) / cellWidth).toInt()

            if (shipNumber < shipSizes.size) {
                newPlayerShip = StudentShip(row, column, row, column + shipSizes[shipNumber] - 1)

                if (!playerShipList.any { it.overlaps(newPlayerShip) } && newPlayerShip.right < colCount) {
                    shipNumber += 1
                    tempPlayerShipList.add(newPlayerShip)
                }
            }

            return true
        }

    }


    companion object {
        val tempPlayerShipList = mutableListOf<StudentShip>()
        val playerShipList: List<StudentShip> get() = tempPlayerShipList
    }


}