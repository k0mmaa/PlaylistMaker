package com.example.playlistmaker.player.ui

sealed interface PlaylistAddingResult {
    data class Success(val playlistName: String) : PlaylistAddingResult
    data class AlreadyExists(val playlistName: String) : PlaylistAddingResult
}
