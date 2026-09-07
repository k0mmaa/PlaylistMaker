package com.example.playlistmaker.media.data.repository

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.data.db.PlaylistDao
import com.example.playlistmaker.data.db.PlaylistTrackDao
import com.example.playlistmaker.media.data.converter.PlayListConverter
import com.example.playlistmaker.media.data.converter.TrackConverter
import com.example.playlistmaker.media.domain.api.PlaylistRepository
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PlaylistRepositoryImpl(
    private val context: Context,
    private val playlistDao: PlaylistDao,
    private val converter: PlayListConverter,
    private val playlistTrackDao: PlaylistTrackDao,
    private val trackConverter: TrackConverter
) : PlaylistRepository {

    override suspend fun saveImageToInternalStorage(uri: Uri): String = withContext(Dispatchers.IO) {
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "my_photos")
        if (!filePath.exists()) filePath.mkdirs()

        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        file.absolutePath
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistDao.insertNewPlaylist(converter.map(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists().map { entities ->
            entities.map { entity ->
                converter.map(entity)
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        playlistTrackDao.insertTrack(trackConverter.mapToPlaylistTrackEntity(track))
        val updatedPlaylist = playlist.copy(
            trackIds = playlist.trackIds + track.trackId,
            tracksCount = playlist.trackIds.size + 1
        )
        playlistDao.insertNewPlaylist(converter.map(updatedPlaylist))
    }
}
