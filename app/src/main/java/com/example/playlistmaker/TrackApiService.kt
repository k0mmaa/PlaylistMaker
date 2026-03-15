package com.example.playlistmaker


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface TrackApiService {

    @GET("search")
    fun searchTrack(
        @Query("term") query: String,
        @Query("entity") entity: String = "song"
    ): Call<TrackResponse>
}
