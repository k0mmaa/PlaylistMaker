package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.models.ThemeSettings
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<SettingsState>()
    fun observeState(): LiveData<SettingsState> = stateLiveData

    init {
        stateLiveData.value = SettingsState(settingsInteractor.getThemeSettings())
    }

    fun switchTheme(isDark: Boolean) {
        val newSettings = ThemeSettings(isDark)
        settingsInteractor.updateThemeSetting(newSettings)
        stateLiveData.value = SettingsState(newSettings)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun contactSupport() {
        sharingInteractor.contactSupport()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }
}
