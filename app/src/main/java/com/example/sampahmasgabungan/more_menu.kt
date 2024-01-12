package com.example.sampahmasgabungan

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class more_menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_menu)

        val backButton = findViewById<ImageButton>(R.id.iBack)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val exchangeButton = findViewById<ImageButton>(R.id.iExchange)
        exchangeButton.setOnClickListener {
            val Intent = Intent(this,activity_QR_NFC::class.java)
            startActivity(Intent)
        }

        val dailyMissionButton = findViewById<ImageButton>(R.id.iDailyMission)
        dailyMissionButton.setOnClickListener {
            val Intent = Intent(this,Daily_Mission::class.java)
            startActivity(Intent)
        }

        val greenMarketButton = findViewById<ImageButton>(R.id.iGreenMarket)
        greenMarketButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("fragmentToLoad", "MarketView")
            }
            startActivity(intent)
        }
    }
}