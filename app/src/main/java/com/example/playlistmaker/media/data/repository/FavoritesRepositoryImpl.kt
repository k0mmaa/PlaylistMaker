package com.example.playlistmaker.media.data.repository

import com.example.playlistmaker.data.db.TrackDao
import com.example.playlistmaker.media.data.converter.TrackConverter
import com.example.playlistmaker.media.domain.api.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val trackDao: TrackDao,
    private val converter: TrackConverter
): FavoritesRepository {

    override suspend fun addTrackToFavorites(track: Track): Resource<Unit> {
        return runCatching { trackDao.insertNewTrack(converter.map(track)) }
            .fold(
                onSuccess = { Resource.Success(Unit) },
                onFailure = { Resource.Error("Ошибка при добавлении в избранное") }
            )
    }

    override suspend fun removeTrackFromFavorites(trackId: Long): Resource<Unit> {
        return runCatching { trackDao.deleteTrackById(trackId) }
            .fold(
                onSuccess = { Resource.Success(Unit) },
                onFailure = { Resource.Error("Ошибка при удалении из избранного") }
            )
    }

    override fun getFavoritesTracks(): Flow<List<Track>> {
        return trackDao.getTracks()
            .map { entities ->
                entities.map { entity ->
                    converter.map(entity)
                }
            }
            .flowOn(Dispatchers.IO)
    }
}
