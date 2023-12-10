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
import org.example.student.battleshipgame.*
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid
import uk.ac.bournemouth.ap.battleshiplib.GuessCell
import kotlin.math.floor

class PlayerGuessGridView : GridViewBase {

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

    override val colCount: Int get() = grid.columns
    override val rowCount: Int get() = grid.rows


    private val gridListener = BattleshipGrid.BattleshipGridListener { _, _, _ ->
        invalidate()
    }
    var grid: StudentBattleshipGrid = StudentBattleshipGrid()
        set(value) {
            field.removeOnGridChangeListener(gridListener)
            field = value
            value.addOnGridChangeListener(gridListener)
            onSizeChanged(width, height, width, height)
            invalidate()
        }

    init {

        grid.addOnGridChangeListener(gridListener)

    }

    private val hitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.RED
    }
    private val sunkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLUE
    }
    private val missPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.LTGRAY
    }

    override fun onDraw(canvas: Canvas) {
        drawGrid(canvas)
        drawCell(canvas)
    }

    /** Draws a red circle for hit, blue for sunk and grey for miss on the board when the player takes a shot*/
    private fun drawCell(canvas: Canvas){
        for (column in 0 until colCount) {
            for (row in 0 until rowCount) {
                val cell = grid[column, row]
                val canvasX = column * cellWidth + gridLeft
                val canvasY = row * cellWidth + gridTop
                when (cell) {
                    is GuessCell.HIT -> {
                        canvas.drawCircle(canvasX + 0.5f * cellWidth, canvasY + 0.5f * cellWidth, 0.4f * cellWidth, hitPaint)
                    }
                    GuessCell.MISS -> {
                        canvas.drawCircle(canvasX + 0.5f * cellWidth, canvasY + 0.5f * cellWidth, 0.4f * cellWidth, missPaint)
                    }
                    is GuessCell.SUNK -> {
                        canvas.drawCircle(canvasX + 0.5f * cellWidth, canvasY + 0.5f * cellWidth, 0.4f * cellWidth, sunkPaint)
                    }
                    GuessCell.UNSET -> {
                    }
                    else -> {
                    }
                }
            }
        }
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

        /** When the player clicks on the grid it will call the shootAt function on the opponents grid if the shot is valid*/
        override fun onSingleTapUp(ev: MotionEvent): Boolean {

            val column = floor((ev.x - gridLeft) / cellWidth).toInt()
            val row = floor((ev.y - gridTop) / cellWidth).toInt()

            //checks if that coordinate has already been guessed
            if (grid[column, row] == GuessCell.UNSET) {
                grid.shootAt(column, row)
                playerTurn++
            }
            return true
        }

    }// End of myGestureListener class

    companion object {
        var playerTurn = 0
    }

}