package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_settings)

        val btnBackMainActivity = findViewById<ImageButton>(R.id.back_button)

        btnBackMainActivity.setOnClickListener {
            val backToMainActivity = Intent(this, MainActivity::class.java )
            startActivity(backToMainActivity)
        }
    }
}