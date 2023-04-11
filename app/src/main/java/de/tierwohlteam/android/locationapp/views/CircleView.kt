package de.tierwohlteam.android.locationapp.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CircleView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var circleX: Float = 0f
    private var circleY: Float = 0f

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var radius: Float = 0f

    fun setCircle(centerX: Float, centerY: Float, radius: Float) {
        this.centerX = centerX
        this.centerY = centerY
        this.radius = radius
        invalidate()
    }

    fun setCirclePosition(x: Float, y: Float) {
        circleX = x
        circleY = y
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawCircle(centerX, centerY, radius, paint)
    }
}
