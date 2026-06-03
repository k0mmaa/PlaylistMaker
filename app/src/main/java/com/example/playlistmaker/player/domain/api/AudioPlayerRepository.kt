package com.example.playlistmaker.player.domain.api

interface AudioPlayerRepository {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun release()
    fun getCurrentPosition(): Int
}
