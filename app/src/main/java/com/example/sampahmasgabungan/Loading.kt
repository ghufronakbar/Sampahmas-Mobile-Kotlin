package com.example.sampahmasgabungan

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView

class Loading : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val tvStatus: TextView = findViewById(R.id.tvStatus)
        val tvToMenu: TextView = findViewById(R.id.backtoMenu)
        val tvStatusDesc: TextView = findViewById(R.id.tvStatusDesc)
        val progressBar2: ProgressBar = findViewById(R.id.progressBar2)
        val iSuccess: ImageView = findViewById(R.id.iSuccess)

        tvToMenu.setOnClickListener {
            val Intent = Intent(this,MainActivity::class.java)
            startActivity(Intent)
        }

        val handler = Handler()

        handler.postDelayed({
            // Change tvStatus text to "Success"
            tvStatus.text = "Success"

            // Change tvStatusDesc text to "Data validation success by employees"
            tvStatusDesc.text = "Data validation success by employees"

            // Remove ProgressBar
            progressBar2.visibility = View.GONE

            // Show ImageView
            iSuccess.visibility = View.VISIBLE

            tvToMenu.visibility = View.VISIBLE
        }, 2000) // Delay for 2 seconds (2000 milliseconds)
    }
}