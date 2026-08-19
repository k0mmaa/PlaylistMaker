package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    //метод @Insert для добавления трека в таблицу с избранными треками;
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewTrack(trackEntity: TrackEntity)

    //метод @Delete для удаления трека из таблицы избранных треков;
    @Delete()
    suspend fun deleteTrackEntity(trackEntity: TrackEntity)

    // Удаление трека из таблицы избранных треков по ID
    @Query("DELETE FROM tracks WHERE id = :trackId")
    suspend fun deleteTrackById(trackId: Long)

    //метод @Query для получения списка со всеми треками, добавленными в избранное;
    // Сортируем по времени добавления: последние добавленные сверху
    @Query("SELECT * FROM tracks ORDER BY additionTimestamp DESC")
    fun getTracks(): Flow<List<TrackEntity>>


    //метод @Query для получения списка идентификаторов всех треков, которые добавлены в избранное.
    @Query("SELECT id FROM tracks")
    suspend fun getFavoriteTrackIds(): List<Long>

    // Проверка, есть ли трек в избранном (по ID)
    @Query("SELECT id FROM tracks WHERE id = :id")
    suspend fun isTrackInFavorites(id: Long): Long?

}
