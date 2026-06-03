package com.example.playlistmaker.domain.api

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<com.example.playlistmaker.domain.models.Track>?, errorMessage: String?)
    }
}
