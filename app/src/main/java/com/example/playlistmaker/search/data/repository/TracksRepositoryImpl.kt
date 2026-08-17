package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.data.db.TrackDao
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackDao: TrackDao) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 -> {
                val tracks = (response as TrackSearchResponse).results?.map {
                    Track(
                        trackId = it.trackId,
                        trackName = it.trackName ?: "",
                        artistName = it.artistName ?: "",
                        trackTimeMillis = it.trackTimeMillis,
                        artworkUrl100 = it.artworkUrl100 ?: "",
                        collectionName = it.collectionName ?: "",
                        releaseDate = it.releaseDate ?: "",
                        primaryGenreName = it.primaryGenreName ?: "",
                        country = it.country ?: "",
                        previewUrl = it.previewUrl
                    )
                } ?: emptyList()

                val favoriteIds = trackDao.getFavoriteTrackIds()

                val tracksWithFavorites = tracks.map { track ->
                    track.apply {
                        isFavorite = favoriteIds.contains(track.trackId)
                    }
                }

                emit(Resource.Success(tracksWithFavorites))
            }
            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }.flowOn(Dispatchers.IO)
}
