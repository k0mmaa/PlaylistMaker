package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val audioPlayerInteractor: AudioPlayerInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerState>(PlayerState.Default)
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private val handler = Handler(Looper.getMainLooper())
    private val dateFormat by lazy(mode = LazyThreadSafetyMode.NONE) { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            val currentTime = dateFormat.format(audioPlayerInteractor.getCurrentPosition().toLong())
            stateLiveData.value = PlayerState.Playing(currentTime)
            handler.postDelayed(this, UPDATE_DELAY)
        }
    }

    fun preparePlayer(url: String?) {
        audioPlayerInteractor.preparePlayer(
            url = url,
            onPrepared = {
                stateLiveData.postValue(PlayerState.Prepared(dateFormat.format(0L)))
            },
            onCompletion = {
                handler.removeCallbacks(updateTimeRunnable)
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
        handler.post(updateTimeRunnable)
    }

    fun pausePlayer() {
        audioPlayerInteractor.pausePlayer()
        handler.removeCallbacks(updateTimeRunnable)
        val currentTime = dateFormat.format(audioPlayerInteractor.getCurrentPosition().toLong())
        stateLiveData.value = PlayerState.Paused(currentTime)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(updateTimeRunnable)
        audioPlayerInteractor.release()
    }

    companion object {
        private const val UPDATE_DELAY = 300L
    }
}
