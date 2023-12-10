package uk.ac.bournemouth.ap.battleships

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes

abstract class GridViewBase : View {
    constructor(context: Context?) : super(context) {
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        context.withStyledAttributes(attrs, R.styleable.GridViewBase, defStyle, R.style.Widget_Theme_Battleships_GridView) {
            gridBackgroundPaint.color = this.getColor(R.styleable.GridViewBase_gridBackgroundColor, Color.CYAN)
            gridLinePaint.color = this.getColor(R.styleable.GridViewBase_gridLineColor, Color.BLACK)
            gridLinePaint.strokeWidth = this.getDimension(R.styleable.GridViewBase_gridLineWidth, 1f)
        }
    }


    abstract val colCount: Int
    abstract val rowCount: Int

    var cellWidth = 0f
    var offsetLeft = 0f
    var offsetTop = 0f


    //paints
    private val gridBackgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val gridLinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val gridHeight get() = rowCount * cellWidth
    private val gridWidth get() = colCount * cellWidth

    val gridTop get() = (paddingTop + offsetTop)
    val gridLeft get() = (paddingLeft + offsetLeft)
    private val gridBottom get() = (paddingTop + offsetTop + gridHeight)
    private val gridRight get() = (paddingLeft + offsetLeft + gridWidth)

    //adjusts for screen size
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val availableWidth = w - paddingLeft - paddingRight - gridLinePaint.strokeWidth
        val availableHeight = h - paddingTop - paddingBottom - gridLinePaint.strokeWidth
        cellWidth = minOf(availableWidth / colCount, availableHeight / rowCount)
        offsetLeft = (availableWidth + gridLinePaint.strokeWidth - gridWidth) / 2
        offsetTop = (availableHeight + gridLinePaint.strokeWidth - gridHeight) / 2

    }

    protected fun drawGrid(canvas: Canvas) {


        //draw the game board
        canvas.drawRect(gridLeft, gridTop, gridRight, gridBottom, gridBackgroundPaint)

        //draw vertical lines
        for (col in 0..colCount) {
            val lineX = gridLeft + (col * cellWidth)
            canvas.drawLine(lineX, gridTop, lineX, gridBottom, gridLinePaint)
        }

        //draw horizontal lines
        for (row in 0..rowCount) {
            val lineY = gridTop + (row * cellWidth)
            canvas.drawLine(gridLeft, lineY, gridRight, lineY, gridLinePaint)
        }


    }


}