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
                stateLiveData.postValue(PlayerState.Prepared(dateFormat.format(0L)))
            },
            onCompletion = {
                stopTimer()
                stateLiveData.postValue(PlayerState.Prepared(dateFormat.format(0L)))
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
        startTimer()
    }

    fun pausePlayer() {
        audioPlayerInteractor.pausePlayer()
        stopTimer()
        val currentTime = dateFormat.format(audioPlayerInteractor.getCurrentPosition().toLong())
        stateLiveData.value = PlayerState.Paused(currentTime)
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (stateLiveData.value is PlayerState.Playing || stateLiveData.value is PlayerState.Prepared || stateLiveData.value is PlayerState.Paused) {
                if (stateLiveData.value is PlayerState.Playing) {
                    val currentTime = dateFormat.format(audioPlayerInteractor.getCurrentPosition().toLong())
                    stateLiveData.value = PlayerState.Playing(currentTime)
                }
                delay(UPDATE_DELAY)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerInteractor.release()
    }

    companion object {
        private const val UPDATE_DELAY = 300L
    }
}
