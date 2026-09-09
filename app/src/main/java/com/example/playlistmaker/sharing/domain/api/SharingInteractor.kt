package com.example.playlistmaker.sharing.domain.api

interface SharingInteractor {
    fun shareApp()
    fun sharePlaylist(playlistInfo: String)
    fun openTerms()
    fun contactSupport()
}
