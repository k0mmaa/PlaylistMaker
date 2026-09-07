package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.FavoritesInteractor
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.util.SingleLiveEvent

class PlayerViewModel(
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerState>(PlayerState.Default)
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private val isFavoriteLiveData = MutableLiveData<Boolean>()
    fun observeIsFavorite(): LiveData<Boolean> = isFavoriteLiveData
    private val playlistsLiveData = MutableLiveData<List<Playlist>>()

    fun observePlaylists(): LiveData<List<Playlist>> = playlistsLiveData

    private val addingResultLiveData = SingleLiveEvent<PlaylistAddingResult>()
    fun observeAddingResult(): LiveData<PlaylistAddingResult> = addingResultLiveData

    private val dateFormat by lazy(mode = LazyThreadSafetyMode.NONE) {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }
    private var timerJob: Job? = null
    private var track: Track? = null

    fun setTrack(track: Track) {
        this.track = track
        checkFavoriteStatus(track.trackId)
        preparePlayer(track.previewUrl)
    }

    private fun checkFavoriteStatus(trackId: Long) {
        viewModelScope.launch {
            favoritesInteractor.getFavoritesTracks().collect { tracks ->
                isFavoriteLiveData.postValue(tracks.any { it.trackId == trackId })
            }
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlists ->
                playlistsLiveData.postValue(playlists)
            }
        }
    }

    private fun preparePlayer(url: String?) {
        audioPlayerInteractor.preparePlayer(
            url = url,
            onPrepared = {
                viewModelScope.launch {
                    renderState(PlayerState.Prepared(dateFormat.format(0L)))
                }
            },
            onCompletion = {
                viewModelScope.launch {
                    stopTimer()
                    renderState(PlayerState.Prepared(dateFormat.format(0L)))
                }
            }
        )
    }

    fun playbackControl() {
        when (stateLiveData.value) {
            is PlayerState.Playing -> pausePlayer()
            is PlayerState.Prepared, is PlayerState.Paused -> startPlayer()
            else -> {}
        }
    }

    private fun startPlayer() {
        audioPlayerInteractor.startPlayer()
        renderState(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    fun pausePlayer() {
        audioPlayerInteractor.pausePlayer()
        stopTimer()
        renderState(PlayerState.Paused(getCurrentPlayerPosition()))
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (stateLiveData.value is PlayerState.Playing) {
                delay(UPDATE_DELAY)
                if (stateLiveData.value is PlayerState.Playing) {
                    renderState(PlayerState.Playing(getCurrentPlayerPosition()))
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    private fun getCurrentPlayerPosition(): String {
        return dateFormat.format(audioPlayerInteractor.getCurrentPosition().toLong())
    }

    private fun renderState(state: PlayerState) {
        stateLiveData.value = state
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        audioPlayerInteractor.release()
    }

    fun onFavoriteClicked() {
        val currentTrack = track ?: return
        val isFavorite = isFavoriteLiveData.value ?: false
        viewModelScope.launch {
            if (isFavorite) {
                favoritesInteractor.removeTrackFromFavorites(currentTrack.trackId)
            } else {
                favoritesInteractor.addTrackToFavorites(currentTrack)
            }
        }
    }

    fun onPlaylistClicked(playlist: Playlist) {
        val currentTrack = track ?: return

        if (playlist.trackIds.contains(currentTrack.trackId)) {

            addingResultLiveData.postValue(PlaylistAddingResult.AlreadyExists(playlist.name))
        } else {

            viewModelScope.launch {
                playlistInteractor.addTrackToPlaylist(playlist, currentTrack)
                addingResultLiveData.postValue(PlaylistAddingResult.Success(playlist.name))
            }
        }
    }



    companion object {
        private const val UPDATE_DELAY = 300L
    }
}
