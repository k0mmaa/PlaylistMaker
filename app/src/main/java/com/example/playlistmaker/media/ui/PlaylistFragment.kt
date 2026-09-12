package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val args: PlaylistFragmentArgs by navArgs()

    private val viewModel: PlaylistViewModel by viewModel {
        parametersOf(args.playlistId)
    }

    private var trackAdapter: TrackAdapter? = null
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initMenuBottomSheet()

        viewModel.loadPlaylistDetails()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.shareButton.setOnClickListener {
            sharePlaylist()
        }

        binding.menuButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.menuShare.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }

        binding.menuDelete.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
        }

        binding.menuEdit.setOnClickListener {
            val currentState = viewModel.state.value
            if (currentState != null) {
                findNavController().navigate(
                    PlaylistFragmentDirections.actionPlaylistFragmentToPlaylistFragmentEdit(currentState.playlist)
                )
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            bindPlaylist(state)
        }

        viewModel.showToast.observe(viewLifecycleOwner) { messageRes ->
            Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show()
        }

        viewModel.navigateBack.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun initMenuBottomSheet() {
        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetMenu)
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> binding.overlay.visibility = View.GONE
                    else -> binding.overlay.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (slideOffset + 1f) / 2f
            }
        })
    }

    private fun initRecyclerView() {
        trackAdapter = TrackAdapter(
            onClick = { track ->
                if (viewModel.clickDebounce()) {
                    findNavController().navigate(
                        PlaylistFragmentDirections.actionPlaylistFragmentToPlayerFragment(track)
                    )
                }
            },
            onLongClick = { track ->
                showDeleteTrackDialog(track)
            }
        )
        binding.tracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksRecyclerView.adapter = trackAdapter
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setMessage(R.string.delete_track_message)
            .setNegativeButton(R.string.no) { _, _ -> }
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.removeTrack(track.trackId)
            }
            .show()
    }

    private fun showDeletePlaylistDialog() {
        val playlistName = viewModel.state.value?.playlist?.name ?: ""
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(R.string.delete_playlist)
            .setMessage(getString(R.string.delete_playlist_message, playlistName))
            .setNegativeButton(R.string.no) { _, _ -> }
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deletePlaylist()
            }
            .show()
    }

    private fun sharePlaylist() {
        viewModel.sharePlaylist(binding.playlistTracksCount.text.toString())
    }

    private fun bindPlaylist(state: PlaylistScreenState) {
        val playlist = state.playlist

        binding.playlistName.text = playlist.name
        binding.playlistDescription.text = playlist.description
        
        val durationText = resources.getQuantityString(
            R.plurals.minutes_count,
            state.totalDurationMinutes,
            state.totalDurationMinutes
        )
        binding.playlistDuration.text = durationText

        val tracksCountText = resources.getQuantityString(
            R.plurals.track_count,
            playlist.tracksCount,
            playlist.tracksCount
        )
        binding.playlistTracksCount.text = tracksCountText

        Glide.with(this)
            .load(File(playlist.imagePath))
            .placeholder(R.drawable.bg_playlist_cover_placeholder)
            .error(R.drawable.bg_playlist_cover_placeholder)
            .into(binding.playlistCoverLarge)

        if (state.tracks.isEmpty()) {
            binding.tracksRecyclerView.visibility = View.GONE
            binding.emptyPlaylistMessage.visibility = View.VISIBLE
        } else {
            binding.tracksRecyclerView.visibility = View.VISIBLE
            binding.emptyPlaylistMessage.visibility = View.GONE
            trackAdapter?.updateTracks(state.tracks)
        }

        binding.playlistItemInfo.tvPlaylistNameBtsh.text = playlist.name
        binding.playlistItemInfo.tvPlaylistTrackCountBtsh.text = tracksCountText
        Glide.with(this)
            .load(File(playlist.imagePath))
            .placeholder(R.drawable.bg_playlist_cover_placeholder)
            .error(R.drawable.bg_playlist_cover_placeholder)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius_player)))
            .into(binding.playlistItemInfo.ivPlaylistCoverBtsh)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
