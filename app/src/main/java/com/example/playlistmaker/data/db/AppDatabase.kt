package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    version = 4,
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class
    ]
)
@TypeConverters(PlaylistDbConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getTrackDao(): TrackDao
    abstract fun getPlaylistDao(): PlaylistDao
    abstract fun getPlaylistTrackDao(): PlaylistTrackDao

}
