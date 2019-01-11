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
