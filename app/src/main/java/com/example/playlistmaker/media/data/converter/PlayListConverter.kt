package com.example.playlistmaker.media.data.converter

import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.media.domain.models.Playlist

class PlayListConverter {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            imagePath = playlist.imagePath,
            trackIds = playlist.trackIds,
            tracksCount = playlist.trackIds.size,
            additionTimestamp = playlist.additionTimestamp
        )
    }

    fun map(entity: PlaylistEntity): Playlist {
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            imagePath = entity.imagePath,
            trackIds = entity.trackIds,
            tracksCount = entity.tracksCount,
            additionTimestamp = entity.additionTimestamp
        )
    }
}

