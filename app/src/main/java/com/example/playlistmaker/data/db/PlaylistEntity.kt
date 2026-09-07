package com.example.playlistmaker.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Playlist")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name="name_playlist")
    val name: String,
    @ColumnInfo(name="description")
    val description: String,
    @ColumnInfo(name="uri_to_image")
    val imagePath: String,
    @ColumnInfo(name="tracks_ids")
    val trackIds: List<Long>,
    @ColumnInfo(name="tracks_count")
    val tracksCount: Int,
    val additionTimestamp: Long // время добавления в избранное, в формате UNIX
    )