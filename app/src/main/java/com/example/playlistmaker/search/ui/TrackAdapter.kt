package com.example.playlistmaker.search.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.models.Track

class TrackAdapter (
    private val onClick: (Track) -> Unit,
    private val onLongClick: ((Track) -> Unit)? = null
) : RecyclerView.Adapter<TrackViewHolder>(){

    private var trackList: List<Track> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            onClick(track)
        }

        holder.itemView.setOnLongClickListener {
            onLongClick?.invoke(track)
            true
        }
    }

    override fun getItemCount(): Int = trackList.size

    fun updateTracks(newList: List<Track>) {
        trackList = newList
        notifyDataSetChanged()
    }
}
