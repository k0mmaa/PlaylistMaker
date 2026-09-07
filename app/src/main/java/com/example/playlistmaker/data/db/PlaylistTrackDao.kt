package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Delete
    suspend fun deleteTrack(track: PlaylistTrackEntity)

    @Query("DELETE FROM playlistTrack WHERE id = :trackId")
    suspend fun deleteTrackById(trackId: Long)

    @Query("SELECT id FROM playlistTrack")
    suspend fun getTracksIds(): List<Long>
}
