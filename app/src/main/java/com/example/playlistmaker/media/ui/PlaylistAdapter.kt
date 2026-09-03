package com.example.playlistmaker.media.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.media.domain.models.Playlist


class PlaylistAdapter (
    private var playlists: List<Playlist> = emptyList(),
    val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(parent)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)

        //слушатель нажатия на playlist
        holder.itemView.setOnClickListener {
            onClick(playlist)
        }
    }

    override fun getItemCount(): Int = playlists.size

    fun updatePlaylist(newList: List<Playlist>) {
        playlists = newList
        notifyDataSetChanged()
    }
}
