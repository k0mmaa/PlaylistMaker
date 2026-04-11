package com.example.playlistmaker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track (

    val trackId: Long, // Уникальный идентификатор трека - добавил для истории
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String, //Название альбома (collectionName) (если его нет, то эту информацию на экране не показываем),
    val releaseDate: String, //Год релиза трека (releaseDate) (если его нет, то эту информацию на экране не показываем),
    val primaryGenreName: String, // Style
    val country: String, //Страна исполнителя
) : Parcelable