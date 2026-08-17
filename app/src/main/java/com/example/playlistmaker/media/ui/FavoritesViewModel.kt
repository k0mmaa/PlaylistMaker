package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.FavoritesInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel()
{
    private val _state = MutableLiveData<FavoritesState>(FavoritesState.Loading)
    val state: LiveData<FavoritesState> = _state

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _state.value = FavoritesState.Loading
            favoritesInteractor.getFavoritesTracks()
                .catch { exception ->
                    _state.value = FavoritesState.Error(exception.message ?: "Ошибка загрузки")
                }
                .collect { tracks ->
                    _state.value = if (tracks.isEmpty()) {
                        FavoritesState.Empty
                    } else {
                        FavoritesState.Content(tracks)
                    }
                }
        }
    }

    fun removeTrackFromFavorites(track: Track) {
        viewModelScope.launch {
            favoritesInteractor.removeTrackFromFavorites(track)
        }
    }
}

sealed class FavoritesState{
    object Loading : FavoritesState()
    data class Content(val tracks: List<Track>) : FavoritesState()
    object Empty : FavoritesState()
    data class Error(val message: String) : FavoritesState()
}
