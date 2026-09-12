package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertNewPlaylist(playlistEntity: PlaylistEntity)

        @Delete()
        suspend fun deletePlaylistEntity(playlistEntity: PlaylistEntity)

        @Query("DELETE FROM Playlist WHERE id = :playlistId")
        suspend fun deletePlaylistById(playlistId: Int)

        @Query("SELECT * FROM Playlist WHERE id = :id")
        suspend fun getPlaylist(id: Int): PlaylistEntity

        @Query("SELECT * FROM Playlist")
        suspend fun getAllPlaylists(): List<PlaylistEntity>

        @Query("SELECT * FROM Playlist WHERE id = :id")
        fun getPlaylistById(id: Int): Flow<PlaylistEntity>

        @Query("SELECT * FROM Playlist ORDER BY additionTimestamp DESC")
        fun getPlaylists(): Flow<List<PlaylistEntity>>

    }