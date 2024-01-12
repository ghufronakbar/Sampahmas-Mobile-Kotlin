package com.example.sampahmasgabungan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class Daily_Mission : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_mission)

        val backbutton = findViewById<ImageButton>(R.id.iBack)
        backbutton.setOnClickListener {
            onBackPressed()
        }
    }
}