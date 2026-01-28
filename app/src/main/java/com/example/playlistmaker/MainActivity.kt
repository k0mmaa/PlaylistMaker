package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        val btnSearch = findViewById<MaterialButton>(R.id.search_button)
        val btnMedia = findViewById<MaterialButton>(R.id.media_button)
        val btnSettings = findViewById<MaterialButton>(R.id.settings_button)

        btnSearch.setOnClickListener {
            val navigateToSearchActivity = Intent(this, SearchActivity::class.java)
            startActivity(navigateToSearchActivity)
        }

        btnMedia.setOnClickListener {
            val navigateToMediaActivity = Intent(this, MediaActivity::class.java)
            startActivity(navigateToMediaActivity)
        }

        btnSettings.setOnClickListener {
            val navigateToSettingsActivity = Intent(this, SettingsActivity::class.java)
            startActivity(navigateToSettingsActivity)
        }
    }
}