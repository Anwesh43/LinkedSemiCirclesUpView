package com.anwesh.uiprojects.linkedsemicirclesupview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.semicirclesupview.SemiCirclesUpView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SemiCirclesUpView.create(this)
    }
}
