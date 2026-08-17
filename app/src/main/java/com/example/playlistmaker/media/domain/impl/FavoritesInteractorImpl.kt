package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.api.FavoritesInteractor
import com.example.playlistmaker.media.domain.api.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
): FavoritesInteractor {

    override suspend fun addTrackToFavorites(track: Track): Resource<Unit> {
        return favoritesRepository.addTrackToFavorites(track)
    }
    override suspend fun removeTrackFromFavorites(track: Track): Resource<Unit> {
        return favoritesRepository.removeTrackFromFavorites(track)
    }
    override fun getFavoritesTracks(): Flow<List<Track>> {
        return favoritesRepository.getFavoritesTracks()
    }
}