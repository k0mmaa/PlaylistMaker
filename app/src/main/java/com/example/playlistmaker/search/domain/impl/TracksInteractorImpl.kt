package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            try {
                repository.searchTracks(expression, object : TracksRepository.TracksConsumer {
                    override fun consume(foundTracks: List<com.example.playlistmaker.search.domain.models.Track>?, errorMessage: String?) {
                        consumer.consume(foundTracks, errorMessage)
                    }
                })
            } catch (e: Exception) {
                consumer.consume(null, e.message ?: "Unknown error")
            }
        }
    }
}
