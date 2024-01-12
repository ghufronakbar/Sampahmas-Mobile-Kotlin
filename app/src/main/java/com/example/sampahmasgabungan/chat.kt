package com.example.sampahmasgabungan

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText
import androidx.core.content.res.ResourcesCompat
import android.content.res.Resources
import android.widget.ImageButton

private val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

class chat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val generateChat = findViewById<ImageView>(R.id.generateChat)
        val messageChat = findViewById<TextInputEditText>(R.id.messageChat)
        val llChat = findViewById<LinearLayout>(R.id.llChat)

        val backButton = findViewById<ImageButton>(R.id.iBack)
        backButton.setOnClickListener {
            onBackPressed()
        }

        generateChat.setOnClickListener {
            val cardView = CardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.END
                    topMargin = 20.dp
                    marginEnd = 20.dp
                }
                setCardBackgroundColor(Color.parseColor("#71CFB9"))
                radius = 20.dp.toFloat()
            }

            val textView = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setTypeface(ResourcesCompat.getFont(context, R.font.outfit_light))
                setPadding(20.dp, 20.dp, 20.dp, 20.dp)
                setTextColor(Color.WHITE)
                text = messageChat.text.toString()
            }

            cardView.addView(textView)
            llChat.addView(cardView)
        }

    }
}