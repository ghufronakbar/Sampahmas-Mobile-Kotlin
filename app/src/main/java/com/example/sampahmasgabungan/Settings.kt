package com.example.sampahmasgabungan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.google.firebase.auth.FirebaseAuth

class Settings : AppCompatActivity() {
        private lateinit var auth: FirebaseAuth

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_settings)

            auth = FirebaseAuth.getInstance()

            val logoutButton: Button = findViewById(R.id.bLogout)
            logoutButton.setOnClickListener {
                logout()
            }

            val backButton: ImageButton = findViewById(R.id.iBack)
            backButton.setOnClickListener {
                onBackPressed()
            }
        }

        private fun logout() {
            auth.signOut()
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

    }