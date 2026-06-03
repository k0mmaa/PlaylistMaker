package com.example.playlistmaker.search.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {

    private val historyKey = "track_history"
    private val maxSize = 10

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(historyKey, null) ?: return emptyList()
        val trackDtos = gson.fromJson(json, Array<TrackDto>::class.java) ?: return emptyList()
        return trackDtos.map { mapDtoToDomain(it) }
    }

    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeIf { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > maxSize) {
            history.removeAt(history.lastIndex)
        }
        
        val historyDtos = history.map { mapDomainToDto(it) }
        sharedPreferences.edit {
            putString(historyKey, gson.toJson(historyDtos))
        }
    }

    override fun clearHistory() {
        sharedPreferences.edit {
            remove(historyKey)
        }
    }

    private fun mapDtoToDomain(dto: TrackDto): Track {
        return Track(
            trackId = dto.trackId,
            trackName = dto.trackName,
            artistName = dto.artistName,
            trackTimeMillis = dto.trackTimeMillis,
            artworkUrl100 = dto.artworkUrl100,
            collectionName = dto.collectionName ?: "",
            releaseDate = dto.releaseDate ?: "",
            primaryGenreName = dto.primaryGenreName,
            country = dto.country,
            previewUrl = dto.previewUrl
        )
    }

    private fun mapDomainToDto(track: Track): TrackDto {
        return TrackDto(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }
}
