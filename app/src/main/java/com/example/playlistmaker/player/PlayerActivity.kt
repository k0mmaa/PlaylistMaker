package com.example.playlistmaker.player

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.PlayerState
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.player.ui.PlayerViewModelFactory
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val INPUT_TRACK_JSON = "track_json"
    }

    private lateinit var viewModel: PlayerViewModel
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private lateinit var backgroundImageView: ImageView
    private lateinit var songNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var playImageView: ImageView
    private lateinit var playbackTimeTextView: TextView
    private lateinit var trackTimeMillisValueTextView: TextView
    private lateinit var collectionNameValueTextView: TextView
    private lateinit var releaseDateValueTextView: TextView
    private lateinit var countryNameValueTextView: TextView
    private lateinit var primaryGenreNameValueTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)

        initViews()

        val trackJson = intent.getStringExtra(INPUT_TRACK_JSON)
        val track = if (trackJson != null) Gson().fromJson(trackJson, Track::class.java) else null

        viewModel = ViewModelProvider(this, PlayerViewModelFactory())[PlayerViewModel::class.java]

        if (track != null) {
            bindTrack(track)
            viewModel.preparePlayer(track.previewUrl)
        } else {
            Toast.makeText(this, "Track data is missing", Toast.LENGTH_SHORT).show()
            finish()
        }

        viewModel.observeState().observe(this) { state ->
            render(state)
        }

        playImageView.setOnClickListener {
            viewModel.playbackControl()
        }

        findViewById<Toolbar>(R.id.tool_bar).setNavigationOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        val mainView = findViewById<View>(R.id.parent_layout_media)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        backgroundImageView = findViewById(R.id.backgroundImageView)
        songNameTextView = findViewById(R.id.songName)
        artistNameTextView = findViewById(R.id.artistName)
        playImageView = findViewById(R.id.play)
        playbackTimeTextView = findViewById(R.id.playback_time)
        trackTimeMillisValueTextView = findViewById(R.id.trackTimeMillisValue)
        collectionNameValueTextView = findViewById(R.id.collectionNameValue)
        releaseDateValueTextView = findViewById(R.id.releaseDateValue)
        primaryGenreNameValueTextView = findViewById(R.id.primaryGenreNameValue)
        countryNameValueTextView = findViewById(R.id.countryNameValue)
    }

    private fun bindTrack(track: Track) {
        songNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        playbackTimeTextView.text = "00:00"
        trackTimeMillisValueTextView.text = dateFormat.format(track.trackTimeMillis)
        collectionNameValueTextView.text = track.collectionName
        releaseDateValueTextView.text = track.releaseDate.take(4)
        primaryGenreNameValueTextView.text = track.primaryGenreName
        countryNameValueTextView.text = track.country

        val highResArtworkUrl = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(this)
            .load(highResArtworkUrl)
            .placeholder(R.drawable.no_replay)
            .transform(CenterCrop(), RoundedCorners(dpToPx(8f, this.resources)))
            .into(backgroundImageView)
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Playing -> {
                playImageView.setImageResource(R.drawable.ic_pause_btn)
                playbackTimeTextView.text = state.playbackTime
            }
            is PlayerState.Prepared -> {
                playImageView.setImageResource(R.drawable.ic_play_btn)
                playbackTimeTextView.text = state.playbackTime
            }
            is PlayerState.Paused -> {
                playImageView.setImageResource(R.drawable.ic_play_btn)
                playbackTimeTextView.text = state.playbackTime
            }
            is PlayerState.Default -> {
                playImageView.setImageResource(R.drawable.ic_play_btn)
                playbackTimeTextView.text = "00:00"
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun dpToPx(dp: Float, resource: Resources): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resource.displayMetrics).toInt()
    }
}
