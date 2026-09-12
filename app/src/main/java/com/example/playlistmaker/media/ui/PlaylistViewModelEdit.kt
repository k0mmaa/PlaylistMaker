package com.example.playlistmaker.media.ui

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistViewModelEdit(
    private val playlist: Playlist,
    interactor: PlaylistInteractor
) : PlaylistViewModelCreate(interactor) {

    override fun savePlaylist(name: String, description: String, imageUri: Uri?) {
        viewModelScope.launch {
            val imagePath = imageUri?.let { interactor.saveImageToInternalStorage(it) } ?: playlist.imagePath
            val updatedPlaylist = playlist.copy(
                name = name,
                description = description,
                imagePath = imagePath
            )
            interactor.updatePlaylist(updatedPlaylist)
            _playlistCreated.postValue(true)
        }
    }
}
