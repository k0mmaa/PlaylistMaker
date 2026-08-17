package com.example.playlistmaker.player.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator

class PlayerViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(
                Creator.provideAudioPlayerInteractor(),
                Creator.provideFavoritesInteractor(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
