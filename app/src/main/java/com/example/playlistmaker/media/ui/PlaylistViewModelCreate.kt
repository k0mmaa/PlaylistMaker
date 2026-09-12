package com.example.playlistmaker.media.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch

open class PlaylistViewModelCreate(
    protected val interactor: PlaylistInteractor
) : ViewModel() {

    protected val _playlistCreated = MutableLiveData<Boolean>()
    val playlistCreated: LiveData<Boolean> = _playlistCreated

    open fun savePlaylist(name: String, description: String, imageUri: Uri?) {
        viewModelScope.launch {
            val imagePath = imageUri?.let { interactor.saveImageToInternalStorage(it) } ?: ""
            val playlist = Playlist(
                id = null,
                name = name,
                description = description,
                imagePath = imagePath,
                trackIds = emptyList(),
                tracksCount = 0,
                additionTimestamp = System.currentTimeMillis()
            )
            interactor.addPlaylist(playlist)
            _playlistCreated.postValue(true)
        }
    }
}