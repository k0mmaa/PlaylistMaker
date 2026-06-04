package com.example.playlistmaker.settings

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.main.App
import com.example.playlistmaker.settings.ui.SettingsState
import com.example.playlistmaker.settings.ui.SettingsViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModelFactory
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.parent_layout_settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this, SettingsViewModelFactory(this))[SettingsViewModel::class.java]

        val btnBackMainActivity = findViewById<MaterialToolbar>(R.id.tool_bar)
        val btnSharedApp = findViewById<LinearLayout>(R.id.liner_icon_shared_app)
        val btnEmailSupport = findViewById<LinearLayout>(R.id.liner_icon_support)
        val btnUserAgreement = findViewById<LinearLayout>(R.id.liner_icon_agreement)
        val themeSwitcher = findViewById<SwitchCompat>(R.id.dark_mode_switch)

        viewModel.observeState().observe(this) { state ->
            render(state, themeSwitcher)
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

    private fun render(state: SettingsState, themeSwitcher: SwitchCompat) {
        themeSwitcher.isChecked = state.themeSettings.isDarkTheme
    }
}
