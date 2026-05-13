package com.example.playlistmaker

import android.content.res.Resources
import android.media.MediaPlayer
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
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val INPUT_TRACK = "track"
        const val delayMills  = 300L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private val dateFormat by lazy {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }

    private lateinit var backgroundImageView: ImageView
    private lateinit var songNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var queueImageView: ImageView
    private lateinit var playImageView: ImageView
    private lateinit var pauseImageView: ImageView
    private lateinit var favoriteImageView: ImageView
    private lateinit var playbackTimeTextView: TextView

    private lateinit var trackTimeMillisValueTextView: TextView

    private lateinit var collectionNameValueTextView: TextView

    private lateinit var releaseDateValueTextView: TextView

    private lateinit var countryNameValueTextView: TextView

    private lateinit var primaryGenreNameValueTextView: TextView

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private var previewUrl: String? = null

    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            playbackTimeTextView.text = dateFormat.format(mediaPlayer.currentPosition.toLong())
            handler.postDelayed(this, delayMills)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)

        val mainView = findViewById<View>(R.id.parent_layout_media)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        queueImageView = findViewById(R.id.queue)
        playImageView = findViewById(R.id.play)
        pauseImageView = findViewById(R.id.pause)
        favoriteImageView = findViewById(R.id.favorite)
        playbackTimeTextView = findViewById(R.id.playback_time)

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(INPUT_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(INPUT_TRACK)
        }

        if (track != null) {
            previewUrl = track.previewUrl
            if (!previewUrl.isNullOrEmpty()) {
                preparePlayer()
            } else {
                playImageView.isEnabled = false
                Toast.makeText(this, "No preview available", Toast.LENGTH_SHORT).show()
            }

            val formattedTime = dateFormat.format(0L)
            backgroundImageView = findViewById(R.id.backgroundImageView)

            val highResArtworkUrl = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

            Glide.with(this)
                .load(highResArtworkUrl)
                .placeholder(R.drawable.no_replay)
                .transform(CenterCrop(), RoundedCorners(dpToPx(8f, this.resources)))
                .into(backgroundImageView)

            songNameTextView = findViewById(R.id.songName)
            songNameTextView.text = track.trackName

            artistNameTextView = findViewById(R.id.artistName)
            artistNameTextView.text = track.artistName

            playbackTimeTextView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(0L)

            trackTimeMillisValueTextView = findViewById(R.id.trackTimeMillisValue)
            trackTimeMillisValueTextView.text = "%02d:%02d".format(track.trackTimeMillis / 60_000, (track.trackTimeMillis / 1_000) % 60)



            collectionNameValueTextView = findViewById(R.id.collectionNameValue)
            collectionNameValueTextView.text = track.collectionName

            releaseDateValueTextView = findViewById(R.id.releaseDateValue)
            releaseDateValueTextView.text = track.releaseDate.take(4)

            primaryGenreNameValueTextView = findViewById(R.id.primaryGenreNameValue)
            primaryGenreNameValueTextView.text = track.primaryGenreName

            countryNameValueTextView = findViewById(R.id.countryNameValue)
            countryNameValueTextView.text = track.country
        }

        playImageView.setOnClickListener {
            playbackControl()
        }

        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun dpToPx(dp: Float, resource: Resources): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resource.displayMetrics).toInt()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playImageView.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(updateTimeRunnable)
            playImageView.setImageResource(R.drawable.ic_play_btn)
            playerState = STATE_PREPARED
            playbackTimeTextView.text = dateFormat.format(0L)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playImageView.setImageResource(R.drawable.ic_pause_btn)
        playerState = STATE_PLAYING
        handler.post(updateTimeRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
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
        mediaPlayer.release()
    }
}
