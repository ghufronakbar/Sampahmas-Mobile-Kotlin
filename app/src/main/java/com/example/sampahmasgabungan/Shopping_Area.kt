package com.example.sampahmasgabungan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class Shopping_Area : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_area)

        val BuyButton = findViewById<Button>(R.id.buyNow)
        BuyButton.setOnClickListener {
            val Intent = Intent(this,CheckOut_Area::class.java)
            startActivity(Intent)
        }

        val backbutton = findViewById<ImageButton>(R.id.iBack)
        backbutton.setOnClickListener {
            onBackPressed()
        }
    }
}