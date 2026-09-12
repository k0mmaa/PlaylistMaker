package com.example.playlistmaker.media.domain.api

import android.net.Uri
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun saveImageToInternalStorage(uri: Uri): String
    suspend fun addPlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track)
    fun getPlaylistById(id: Int): Flow<Playlist>
    fun getTracksByIds(ids: List<Long>): Flow<List<Track>>
    suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Long)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
}