package com.example.playlistmaker.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromTrackIds(trackIds: List<Long>?): String {
        return gson.toJson(trackIds ?: emptyList<Long>())
    }

    @TypeConverter
    fun toTrackIds(trackIdsString: String?): List<Long> {
        if (trackIdsString == null) return emptyList()
        val type = object : TypeToken<List<Long>>() {}.type
        return gson.fromJson(trackIdsString, type)
    }
}