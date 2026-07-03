package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.R
import com.example.playlistmaker.main.App
import com.google.android.material.appbar.MaterialToolbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.parent_layout_settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBackMainActivity = findViewById<MaterialToolbar>(R.id.tool_bar)
        val btnSharedApp = findViewById<LinearLayout>(R.id.liner_icon_shared_app)
        val btnEmailSupport = findViewById<LinearLayout>(R.id.liner_icon_support)
        val btnUserAgreement = findViewById<LinearLayout>(R.id.liner_icon_agreement)
        val themeSwitcher = findViewById<SwitchCompat>(R.id.dark_mode_switch)

        // Подписываемся на состояние темы из ViewModel
        viewModel.observeState().observe(this) { state ->
            themeSwitcher.isChecked = state.themeSettings.isDarkTheme
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
            (applicationContext as App).switchTheme(checked)
        }

        btnBackMainActivity.setNavigationOnClickListener {
            finish()
        }

        btnSharedApp.setOnClickListener {
            viewModel.shareApp()
        }

        btnEmailSupport.setOnClickListener {
            viewModel.contactSupport()
        }

        btnUserAgreement.setOnClickListener {
            viewModel.openTerms()
        }
    }
}
