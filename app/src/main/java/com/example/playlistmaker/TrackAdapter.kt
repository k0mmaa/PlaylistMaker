package com.example.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter (
    private var trackList: List<Track>,
    val onClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder,position: Int) {
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