package com.example.playlistmaker.media.data.converter

import com.example.playlistmaker.data.db.TrackEntity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.data.db.PlaylistTrackEntity

class TrackConverter {
    fun map(track: Track): TrackEntity {
        return TrackEntity(
            id = track.trackId,
            highResArtworkUrl = track.artworkUrl100,
            songName = track.trackName,
            artistName = track.artistName,
            collectionNameValue = track.collectionName,
            releaseDateValue = track.releaseDate,
            primaryGenreNameValue = track.primaryGenreName,
            countryNameValue = track.country,
            trackTimeMillisValue = track.trackTimeMillis,
            songUrl = track.previewUrl,
            additionTimestamp = System.currentTimeMillis()
        )
    }

    fun map(entity: TrackEntity): Track {
        return Track(
            trackId = entity.id,
            trackName = entity.songName,
            artistName = entity.artistName,
            trackTimeMillis = entity.trackTimeMillisValue,
            artworkUrl100 = entity.highResArtworkUrl,
            collectionName = entity.collectionNameValue,
            releaseDate = entity.releaseDateValue,
            primaryGenreName = entity.primaryGenreNameValue,
            country = entity.countryNameValue,
            previewUrl = entity.songUrl,
            isFavorite = true
        )
    }

    fun mapToPlaylistTrackEntity(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            id = track.trackId,
            highResArtworkUrl = track.artworkUrl100,
            songName = track.trackName,
            artistName = track.artistName,
            collectionNameValue = track.collectionName,
            releaseDateValue = track.releaseDate,
            primaryGenreNameValue = track.primaryGenreName,
            countryNameValue = track.country,
            trackTimeMillisValue = track.trackTimeMillis,
            songUrl = track.previewUrl
        )
    }
}
