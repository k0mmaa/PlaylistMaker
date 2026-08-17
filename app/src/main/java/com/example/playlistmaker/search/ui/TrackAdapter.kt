package com.example.playlistmaker.search.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.models.Track

class TrackAdapter (
    private var trackList: List<Track> = emptyList(),
    val onClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)

        //слушатель нажатия на трек
        holder.itemView.setOnClickListener {
            onClick(track)
        }
    }

    override fun getItemCount(): Int = trackList.size

    fun updateTracks(newList: List<Track>) {
        trackList = newList
        notifyDataSetChanged()
    }
}
