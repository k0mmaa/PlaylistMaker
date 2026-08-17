package com.example.playlistmaker.media.data.converter


import com.example.playlistmaker.data.db.TrackEntity
import com.example.playlistmaker.search.domain.models.Track


class TrackConverter {
    fun map(track: Track): TrackEntity {
        return TrackEntity(
            id = track.trackId,
                    highResArtworkUrl =track.artworkUrl100,
        songName =track.trackName,
                artistName =track.artistName,
                collectionNameValue =track.collectionName,
                releaseDateValue = track.releaseDate,
        primaryGenreNameValue =track.primaryGenreName,
        countryNameValue =track.country,
        trackTimeMillisValue = track.trackTimeMillis,
        songUrl = track.previewUrl)
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
            previewUrl = entity.songUrl)
    }
}