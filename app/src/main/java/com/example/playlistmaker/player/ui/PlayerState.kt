package com.example.playlistmaker.player.ui

sealed interface PlayerState {
    data class Prepared(val playbackTime: String) : PlayerState
    data class Playing(val playbackTime: String) : PlayerState
    data class Paused(val playbackTime: String) : PlayerState
    object Default : PlayerState
}
