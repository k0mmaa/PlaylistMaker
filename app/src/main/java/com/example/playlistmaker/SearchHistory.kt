package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import androidx.core.content.edit

class SearchHistory(val sharedPreferences: SharedPreferences) {
    private val historyKey = "track_history"
    private val gson = Gson()

    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(historyKey, null) ?: return emptyList()
        val tracksArray = gson.fromJson(json, Array<Track>::class.java)
        return tracksArray?.toList() ?: emptyList()
    }

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeIf { it.trackId == track.trackId }
        history.add(0, track) // Добавляем новый трек в начало списка - индекс0
        if (history.size > 10) { //если больше 10 удаляемм последний
            history.removeAt(history.lastIndex)
        }
        sharedPreferences.edit {
            putString(historyKey, gson.toJson(history))
        }
    }

    fun clearHistory() {
        sharedPreferences.edit{
            remove(historyKey)
        }
    }
}
