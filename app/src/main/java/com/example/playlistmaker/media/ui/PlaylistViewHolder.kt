package com.example.playlistmaker.media.ui

import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.models.Playlist
import java.io.File

class PlaylistViewHolder (parent: ViewGroup): RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)
){

    private val playlistCoverIv: ImageView = itemView.findViewById(R.id.iv_playlist_cover)
    private val playlistNameTv: TextView = itemView.findViewById(R.id.tv_playlist_name)
    private val playlistCountTv: TextView = itemView.findViewById(R.id.tv_playlist_track_count)


    fun bind(playlist: Playlist) {
        playlistNameTv.text = playlist.name
        playlistCountTv.text = itemView.context.resources.getQuantityString(
            R.plurals.track_count,
            playlist.tracksCount,
            playlist.tracksCount
        )

        Glide.with(itemView.context)
            .load(File(playlist.imagePath))
            .placeholder(R.drawable.no_replay)
            .error(R.drawable.no_replay)
            .transform(RoundedCorners(dpToPx(8f, itemView.resources)))
            .into(playlistCoverIv)
    }


    private fun dpToPx(dp: Float, resource: Resources) : Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resource.displayMetrics).toInt()
    }
}