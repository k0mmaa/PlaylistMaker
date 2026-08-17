package com.example.playlistmaker.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity (
    @PrimaryKey(autoGenerate = false)
    val id: Long, //идентификатор трека (первичный ключ),
    @ColumnInfo(name="cover_url")
    val highResArtworkUrl: String,//ссылка на обложку для элемента списка и плеера,
    val songName: String, //название трека,
    val artistName: String, //имя исполнителя,
    @ColumnInfo(name = "album")
    val collectionNameValue: String, //название альбома (если есть),
    val releaseDateValue: String, //год релиза трека,
    @ColumnInfo(name="genre")
    val primaryGenreNameValue: String, //жанр трека,
    @ColumnInfo(name = "country")
    val countryNameValue: String, //страна исполнителя,
    @ColumnInfo(name = "duration")
    val trackTimeMillisValue: Long, //продолжительность трека в формате mm:ss,
    val songUrl: String?, //ссылка на файл для воспроизведения.
)