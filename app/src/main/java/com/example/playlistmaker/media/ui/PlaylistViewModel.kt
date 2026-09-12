package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class PlaylistViewModel(
    private val playlistId: Int,
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var isClickAllowed = true

    private val _state = MutableLiveData<PlaylistScreenState>()
    val state: LiveData<PlaylistScreenState> = _state

    private val _showToast = SingleLiveEvent<Int>()
    val showToast: LiveData<Int> = _showToast

    private val _navigateBack = SingleLiveEvent<Unit>()
    val navigateBack: LiveData<Unit> = _navigateBack

    fun loadPlaylistDetails() {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(playlistId)
                .flatMapLatest { playlist ->
                    playlistInteractor.getTracksByIds(playlist.trackIds).map { tracks ->
                        PlaylistScreenState(playlist, tracks, calculateTotalDuration(tracks))
                    }
                }
                .collect { state ->
                    _state.postValue(state)
                }
        }
    }

    private fun calculateTotalDuration(tracks: List<Track>): Int {
        val totalMillis = tracks.sumOf { it.trackTimeMillis }
        return TimeUnit.MILLISECONDS.toMinutes(totalMillis).toInt()
    }

    fun removeTrack(trackId: Long) {
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(playlistId, trackId)
        }
    }

    fun deletePlaylist() {
        val currentState = _state.value
        if (currentState != null) {
            viewModelScope.launch {
                playlistInteractor.deletePlaylist(currentState.playlist)
                _navigateBack.postValue(Unit)
            }
        }
    }

    fun sharePlaylist(tracksCountText: String) {
        val currentState = _state.value
        if (currentState == null || currentState.tracks.isEmpty()) {
            _showToast.postValue(R.string.empty_playlist_sharing)
        } else {
            val shareInfo = buildString {
                appendLine(currentState.playlist.name)
                if (currentState.playlist.description.isNotEmpty()) {
                    appendLine(currentState.playlist.description)
                }
                appendLine(tracksCountText)
                currentState.tracks.forEachIndexed { index, track ->
                    val duration = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
                    appendLine("${index + 1}. ${track.artistName} - ${track.trackName} ($duration)")
                }
            }
            sharingInteractor.sharePlaylist(shareInfo)
        }
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }
}

data class PlaylistScreenState(
    val playlist: Playlist,
    val tracks: List<Track>,
    val totalDurationMinutes: Int
)
