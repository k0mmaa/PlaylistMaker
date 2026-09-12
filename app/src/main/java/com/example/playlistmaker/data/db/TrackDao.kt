package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewTrack(trackEntity: TrackEntity)

    @Delete()
    suspend fun deleteTrackEntity(trackEntity: TrackEntity)

    @Query("DELETE FROM tracks WHERE id = :trackId")
    suspend fun deleteTrackById(trackId: Long)

    @Query("SELECT * FROM tracks ORDER BY additionTimestamp DESC")
    fun getTracks(): Flow<List<TrackEntity>>


    @Query("SELECT id FROM tracks")
    suspend fun getFavoriteTrackIds(): List<Long>

    @Query("SELECT id FROM tracks WHERE id = :id")
    suspend fun isTrackInFavorites(id: Long): Long?

}
