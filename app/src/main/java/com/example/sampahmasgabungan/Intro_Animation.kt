package com.example.sampahmasgabungan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation

class Intro_Animation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_animation)

        val animationRotate: View = findViewById(R.id.introAnimation)
        val rotate = RotateAnimation(
            -10f, 10f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 500
        rotate.interpolator = LinearInterpolator()
        rotate.repeatMode = Animation.REVERSE
        rotate.repeatCount = Animation.INFINITE

        animationRotate.startAnimation(rotate)

        Handler().postDelayed({
            startActivity(Intent(this, Intro::class.java))
            finish()
        }, 1500)
    }
}