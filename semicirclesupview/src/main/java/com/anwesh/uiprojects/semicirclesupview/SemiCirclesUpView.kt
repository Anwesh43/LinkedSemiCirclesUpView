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
        val r : Float = rGap * (j + 1)
        save()
        translate(0f, -(h/2 + paint.strokeWidth) * scj2)
        drawArc(RectF(-r, -r, r, r), 180f, 180f * scj1, false, paint)
        restore()
    }
    restore()
}

class SemiCirclesUpView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scale.updateScale(dir, semiCircles, semiCircles)
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SCUNode(var i : Int, val state : State = State()) {

        private var next : SCUNode? = null
        private var prev : SCUNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = SCUNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSCUNode(i, state.scale, paint)
            prev?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SCUNode {
            var curr : SCUNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class  SemiCirclesUpStep(var i : Int) {

        private var curr : SCUNode = SCUNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : SemiCirclesUpView) {

        private val animator : Animator = Animator(view)
        private val scu : SemiCirclesUpStep = SemiCirclesUpStep(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            scu.draw(canvas, paint)
            animator.animate {
                scu.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            scu.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity: Activity) : SemiCirclesUpView {
            val view : SemiCirclesUpView = SemiCirclesUpView(activity)
            activity.setContentView(view)
            return view
        }
    }
}