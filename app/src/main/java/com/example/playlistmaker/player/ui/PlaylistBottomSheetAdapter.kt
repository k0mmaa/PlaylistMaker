package com.example.playlistmaker.player.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.media.domain.models.Playlist




class PlaylistBottomSheetAdapter (
    private var playlists: List<Playlist> = emptyList(),
    val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistBottomSheetViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistBottomSheetViewHolder {
        return PlaylistBottomSheetViewHolder(parent)
    }

    override fun onBindViewHolder(holder: PlaylistBottomSheetViewHolder, position: Int) {
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
