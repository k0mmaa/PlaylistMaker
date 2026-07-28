package com.example.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlayerViewModel>()
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = getTrackFromArguments()
        if (track != null) {
            bindTrack(track)
            viewModel.preparePlayer(track.previewUrl)
        } else {
            findNavController().popBackStack()
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        binding.play.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.toolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getTrackFromArguments(): Track? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARGS_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARGS_TRACK)
        }
    }

    private fun bindTrack(track: Track) {
        with(binding) {
            songName.text = track.trackName
            artistName.text = track.artistName
            playbackTime.text = dateFormat.format(0L)
            trackTimeMillisValue.text = dateFormat.format(track.trackTimeMillis)
            collectionNameValue.text = track.collectionName
            releaseDateValue.text = track.releaseDate.take(4)
            primaryGenreNameValue.text = track.primaryGenreName
            countryNameValue.text = track.country

            val highResArtworkUrl = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
            Glide.with(this@PlayerFragment)
                .load(highResArtworkUrl)
                .placeholder(R.drawable.no_replay)
                .transform(CenterCrop(), RoundedCorners(dpToPx(8f)))
                .into(backgroundImageView)
        }
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Playing -> {
                binding.play.setImageResource(R.drawable.ic_pause_btn)
                binding.playbackTime.text = state.playbackTime
            }
            is PlayerState.Paused -> {
                binding.play.setImageResource(R.drawable.ic_play_btn)
                binding.playbackTime.text = state.playbackTime
            }
            is PlayerState.Prepared -> {
                binding.play.setImageResource(R.drawable.ic_play_btn)
                binding.playbackTime.text = state.playbackTime
            }
            is PlayerState.Default -> {
                binding.play.setImageResource(R.drawable.ic_play_btn)
                binding.playbackTime.text = dateFormat.format(0L)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

    companion object {
        const val ARGS_TRACK = "track"
    }
}
