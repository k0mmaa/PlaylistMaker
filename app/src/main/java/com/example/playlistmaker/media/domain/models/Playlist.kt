package com.example.playlistmaker.media.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist (
    val id: Int? = null,
    val name: String,
    val description: String,
    val imagePath: String,
    val trackIds: List<Long>,
    val tracksCount: Int,
    val additionTimestamp: Long,
    ) : Parcelable