package com.example.playlistmaker.media.domain.api

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    //метод для добавления трека в избранное;
    suspend fun addTrackToFavorites(track: Track): Resource<Unit>
    //метод для удаления трека из избранного;
    suspend fun removeTrackFromFavorites(track: Track): Resource<Unit>
    //метод получения списка со всеми треками, добавленными в избранное.
    fun getFavoritesTracks(): Flow<List<Track>>
}