package com.example.playlistmaker.media.domain.api

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun addTrackToFavorites(track: Track): Resource<Unit>
    suspend fun removeTrackFromFavorites(trackId: Long): Resource<Unit>
    fun getFavoritesTracks(): Flow<List<Track>>
}