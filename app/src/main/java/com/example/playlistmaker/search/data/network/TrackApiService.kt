package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackApiService {
    @GET("search")
    fun searchTrack(
        @Query("term") query: String,
        @Query("entity") entity: String = "song"
    ): Call<TrackSearchResponse>
}
