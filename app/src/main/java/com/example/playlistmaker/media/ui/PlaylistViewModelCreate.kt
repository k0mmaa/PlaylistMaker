package com.example.playlistmaker.media.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch

open class PlaylistViewModelCreate(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    fun createPlaylist(name: String, description: String, imageUri: Uri?) {
        viewModelScope.launch {
            val imagePath = imageUri?.let { interactor.saveImageToInternalStorage(it) } ?: ""
            val playlist = Playlist(
                id = 0,
                name = name,
                description = description,
                imagePath = imagePath,
                trackIds = emptyList(),
                tracksCount = 0,
                additionTimestamp = System.currentTimeMillis()
            )
            interactor.addPlaylist(playlist)
        }
    }
}