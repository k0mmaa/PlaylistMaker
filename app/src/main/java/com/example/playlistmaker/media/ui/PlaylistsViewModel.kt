package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel()

{

    private val _state = MutableLiveData<PlaylistsState>(PlaylistsState.Empty)
    val state: LiveData<PlaylistsState> = _state


    init {
        loadPlaylists()
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .catch { exception ->
                    _state.value = PlaylistsState.Error(exception.message ?: "Ошибка загрузки")
                }
                .collect { playlists ->
                    _state.value = if (playlists.isEmpty()) {
                        PlaylistsState.Empty
                    } else {
                        PlaylistsState.Content(playlists)
                    }
                }
        }
    }
}

sealed class PlaylistsState{
    data class Content(val playlists: List<Playlist>) : PlaylistsState()
    object Empty : PlaylistsState()
    data class Error(val message: String) : PlaylistsState()
}
