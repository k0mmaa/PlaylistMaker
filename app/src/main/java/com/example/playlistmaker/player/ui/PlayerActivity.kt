package com.example.playlistmaker.player.ui

import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val INPUT_TRACK = "track"
        const val delayMills = 300L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

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

    private val audioPlayerInteractor = Creator.provideAudioPlayerInteractor()
    
    private var playerState = STATE_DEFAULT
    private var previewUrl: String? = null

    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            playbackTimeTextView.text = dateFormat.format(audioPlayerInteractor.getCurrentPosition().toLong())
            handler.postDelayed(this, delayMills)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)

        initViews()

        val track = getTrackFromIntent()
        if (track != null) {
            bindTrack(track)
            preparePlayer(track.previewUrl)
        }

        playImageView.setOnClickListener {
            playbackControl()
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

    private fun getTrackFromIntent(): Track? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(INPUT_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(INPUT_TRACK)
        }
    }

    private fun bindTrack(track: Track) {
        songNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        playbackTimeTextView.text = dateFormat.format(0L)
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

    private fun preparePlayer(url: String?) {
        if (url.isNullOrEmpty()) {
            playImageView.isEnabled = false
            Toast.makeText(this, "No preview available", Toast.LENGTH_SHORT).show()
            return
        }
        
        audioPlayerInteractor.preparePlayer(
            url = url,
            onPrepared = {
                playImageView.isEnabled = true
                playerState = STATE_PREPARED
            },
            onCompletion = {
                handler.removeCallbacks(updateTimeRunnable)
                playImageView.setImageResource(R.drawable.ic_play_btn)
                playerState = STATE_PREPARED
                playbackTimeTextView.text = dateFormat.format(0L)
            }
        )
    }

    private fun startPlayer() {
        audioPlayerInteractor.startPlayer()
        playImageView.setImageResource(R.drawable.ic_pause_btn)
        playerState = STATE_PLAYING
        handler.post(updateTimeRunnable)
    }

    private fun pausePlayer() {
        audioPlayerInteractor.pausePlayer()
        playImageView.setImageResource(R.drawable.ic_play_btn)
        playerState = STATE_PAUSED
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable)
        audioPlayerInteractor.release()
    }

    private fun dpToPx(dp: Float, resource: Resources): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resource.displayMetrics).toInt()
    }
}
