package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        //val image = findViewById<ImageView>(R.id.poster)  - использовал для отработки учебного материала
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




        /**Способ 1. Реализация анонимного класса
        val btnClickListener: View.OnClickListener = object : View.OnClickListener { override fun onClick(v: View?) {
            Toast.makeText(this@MainActivity, "Нажата кнопка ПОИСК", Toast.LENGTH_SHORT).show()
        } }
        btnSearch.setOnClickListener(this@MainActivity)
        btnMedia.setOnClickListener (this@MainActivity)
        btnSettings.setOnClickListener(this@MainActivity)
    }
    override fun onClick(v: View?){
        when (v?.id){
            R.id.search_button -> {
                //Toast.makeText(this, "Нажата кнопка Поиска", Toast.LENGTH_SHORT).show()

            }
            R.id.media_button ->{
                Toast.makeText(this, "Нажата кнопка Медиатека", Toast.LENGTH_SHORT).show()
            }

            R.id.settings_button -> {
                Toast.makeText(this, "Нажата кнопка Настроек", Toast.LENGTH_SHORT).show()
            }

        }*/
    }
}