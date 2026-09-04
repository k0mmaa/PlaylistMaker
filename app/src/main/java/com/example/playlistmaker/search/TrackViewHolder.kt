package com.example.playlistmaker.search

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
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false))
    {
    var trackNameTv: TextView = itemView.findViewById(R.id.track_name)
    var artistNameTv: TextView = itemView.findViewById(R.id.artist_name)
    var trackTimeTv: TextView = itemView.findViewById(R.id.track_time)
    var artworkUrl100Iv: ImageView = itemView.findViewById(R.id.artwork_url_100)



    fun bind(song: Track){
        trackNameTv.text=song.trackName
        artistNameTv.text=song.artistName
        trackTimeTv.text= SimpleDateFormat("mm:ss", Locale.getDefault()).format(song.trackTimeMillis)

        Glide.with(itemView.context)
            .load(song.artworkUrl100)
            .placeholder(R.drawable.no_replay)
            .transform(RoundedCorners(dpToPx(2f, itemView.resources)))
            .into(artworkUrl100Iv)
    }

        private fun dpToPx(dp: Float, resource: Resources) : Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resource.displayMetrics).toInt()
        }
}