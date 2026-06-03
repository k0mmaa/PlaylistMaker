package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.api.AudioPlayerRepository

class AudioPlayerInteractorImpl(private val repository: AudioPlayerRepository) : AudioPlayerInteractor {

    override fun preparePlayer(url: String?, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        if (url != null) {
            repository.preparePlayer(url, onPrepared, onCompletion)
        }
    }

    override fun startPlayer() {
        repository.startPlayer()
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun release() {
        repository.release()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }
}
