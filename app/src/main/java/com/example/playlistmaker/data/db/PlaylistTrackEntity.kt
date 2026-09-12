package com.example.playlistmaker.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "playlistTrack")
data class PlaylistTrackEntity (
        @PrimaryKey(autoGenerate = false)
        val id: Long,
        @ColumnInfo(name="cover_url")
        val highResArtworkUrl: String,
        val songName: String,
        val artistName: String,
        @ColumnInfo(name = "album")
        val collectionNameValue: String,
        val releaseDateValue: String,
        @ColumnInfo(name="genre")
        val primaryGenreNameValue: String,
        @ColumnInfo(name = "country")
        val countryNameValue: String,
        @ColumnInfo(name = "duration")
        val trackTimeMillisValue: Long,
        val songUrl: String?,
    )