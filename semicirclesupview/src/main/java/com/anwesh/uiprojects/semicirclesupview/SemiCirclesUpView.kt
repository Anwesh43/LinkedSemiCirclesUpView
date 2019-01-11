package com.anwesh.uiprojects.semicirclesupview

/**
 * Created by anweshmishra on 11/01/19.
 */

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.graphics.RectF
import android.view.View
import android.view.MotionEvent

val nodes : Int = 5
val semiCircles : Int = 4
val scDiv : Double = 0.51
val scGap : Float = 0.05f
val strokeFactor : Int = 90
val sizeFactor : Float = 2.7f
val foreColor : Int = Color.parseColor("#4527A0")
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.scaleFactor() : Float = Math.floor(this / scDiv).toFloat()
fun Float.mirrorValue(a : Int, b : Int) : Float = (1 - scaleFactor()) * a.inverse() + scaleFactor() * b.inverse()
fun Float.updateScale(dir : Float, a : Int, b : Int) : Float = mirrorValue(a, b) * scGap * dir

fun Paint.setStrokeStyle(size : Float) {
    style = Paint.Style.STROKE
    strokeWidth = size
    strokeCap = Paint.Cap.ROUND
    color = foreColor
}

fun Canvas.drawSCUNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / sizeFactor
    val sc1 : Float = scale.divideScale(0, 2)
    val sc2 : Float = scale.divideScale(1, 2)
    val rGap : Float = size / (semiCircles)
    paint.setStrokeStyle(Math.min(w, h) / strokeFactor)
    save()
    translate(gap * (i + 1), h/2)
    for (j in 0..(semiCircles - 1)) {
        val scj1 : Float = sc1.divideScale(j, semiCircles)
        val scj2 : Float = sc2.divideScale(semiCircles - 1 - j, semiCircles)
        val r : Float = scGap * (j + 1)
        save()
        translate(0f, -(h/2 + paint.strokeWidth) * scj2)
        drawArc(RectF(-r, -r, r, r), 180f, 180f * scj1, false, paint)
        restore()
    }
    restore()
}

class SemiCirclesUpView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}