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

        //метод @Insert для добавления плейлиста
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertNewPlaylist(playlistEntity: PlaylistEntity)

        //метод @Delete для удаления плейлиста
        @Delete()
        suspend fun deletePlaylistEntity(playlistEntity: PlaylistEntity)

        // Удаление плейлиста из таблицы  по ID
        @Query("DELETE FROM Playlist WHERE id = :playlistId")
        suspend fun deletePlaylistById(playlistId: Int)

        //метод @Query для получения плейлиста по ID
        @Query("SELECT * FROM Playlist ORDER BY additionTimestamp DESC")
        fun getPlaylists(): Flow<List<PlaylistEntity>>




    }