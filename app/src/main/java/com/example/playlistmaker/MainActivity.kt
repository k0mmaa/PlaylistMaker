package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.parent_layout_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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