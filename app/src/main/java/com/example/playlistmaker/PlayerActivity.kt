package com.example.playlistmaker
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale
class PlayerActivity : AppCompatActivity()  {

    private lateinit var backgroundImageView: ImageView
    private lateinit var songNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var queueImageView: ImageView
    private lateinit var playImageView: ImageView
    private lateinit var pauseImageView: ImageView
    private lateinit var favoriteImageView: ImageView
    private lateinit var playbackTimeTextView: TextView
    private lateinit var trackTimeMillisLabelTextView: TextView
    private lateinit var trackTimeMillisValueTextView: TextView
    private lateinit var collectionNameTextView: TextView
    private lateinit var collectionNameValueTextView: TextView
    private lateinit var releaseDateTextView: TextView
    private lateinit var releaseDateValueTextView: TextView
    private lateinit var countryNameTextView: TextView
    private lateinit var countryNameValueTextView: TextView
    private lateinit var primaryGenreNameTextView: TextView
    private lateinit var primaryGenreNameValueTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)


        val mainView = findViewById<View>(R.id.parent_layout_media)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
        queueImageView = findViewById(R.id.queue)
        playImageView = findViewById(R.id.play)
        pauseImageView = findViewById(R.id.pause)
        favoriteImageView = findViewById(R.id.favorite)

        val track = intent.getSerializableExtra("track") as? Track
        if (track != null) {
            backgroundImageView = findViewById(R.id.backgroundImageView)

            val highResArtworkUrl = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

            Glide.with(this)
                .load(highResArtworkUrl)
                .placeholder(R.drawable.no_replay)
                .centerCrop()
                .into(backgroundImageView)

            songNameTextView = findViewById(R.id.songName)
            songNameTextView.text = track.trackName

            artistNameTextView = findViewById(R.id.artistName)
            artistNameTextView.text = track.artistName

            playbackTimeTextView = findViewById(R.id.playback_time)
            playbackTimeTextView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)


            trackTimeMillisLabelTextView = findViewById(R.id.trackTimeMillisLabel)

            trackTimeMillisValueTextView = findViewById(R.id.trackTimeMillisValue)
            trackTimeMillisValueTextView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

            collectionNameTextView = findViewById(R.id.collectionName)

            collectionNameValueTextView = findViewById(R.id.collectionNameValue)
            collectionNameValueTextView.text = track.collectionName

            releaseDateTextView = findViewById(R.id.releaseDate)

            releaseDateValueTextView = findViewById(R.id.releaseDateValue)
            releaseDateValueTextView.text= track.releaseDate.take(4)
            //    SimpleDateFormat("yyyy", Locale.getDefault()).format(track.releaseDate)
            // aHR0cHM6Ly9pLnl0aW1nLmNvbS92aS9NcTNZUWRXVHBxYy9tYXhyZXNkZWZhdWx0LmpwZwo=

            primaryGenreNameTextView = findViewById(R.id.primaryGenreName)

            primaryGenreNameValueTextView = findViewById(R.id.primaryGenreNameValue)
            primaryGenreNameValueTextView.text = track.primaryGenreName


            countryNameTextView = findViewById(R.id.countryName)

            countryNameValueTextView = findViewById(R.id.countryNameValue)
            countryNameValueTextView.text = track.country
        }

        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}