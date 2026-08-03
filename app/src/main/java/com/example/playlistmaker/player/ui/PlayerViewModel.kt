package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val audioPlayerInteractor: AudioPlayerInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerState>(PlayerState.Default)
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private val dateFormat by lazy(mode = LazyThreadSafetyMode.NONE) { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private var timerJob: Job? = null

    fun preparePlayer(url: String?) {
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

    companion object {
        private const val UPDATE_DELAY = 300L
    }
}
