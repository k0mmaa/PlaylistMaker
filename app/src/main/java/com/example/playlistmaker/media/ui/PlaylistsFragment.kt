package com.example.playlistmaker.media.ui

import com.example.playlistmaker.R
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel by viewModel<PlaylistsViewModel>()
    private lateinit var playlistAdapter: PlaylistAdapter

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_fragmentCreatePlaylist)
        }
        initRecyclerView()
        observeState()
        viewModel.loadPlaylists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {
        playlistAdapter = PlaylistAdapter(emptyList()) { playlist ->

        }
        binding.playlistsRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = playlistAdapter
            addItemDecoration(PlaylistGridSpacingDecoration(
                horizontalSpacingPx = dpToPx(8f),
                verticalSpacingPx = dpToPx(16f),
            )
            )
        }
    }
    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }
    private fun renderState(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> {

                binding.playlistsRecyclerView.visibility = View.VISIBLE
                binding.playlistsEmptyView.visibility = View.GONE
                playlistAdapter.updatePlaylist(state.playlists)
            }
            is PlaylistsState.Empty -> {
                binding.playlistsRecyclerView.visibility = View.GONE
                binding.playlistsEmptyView.visibility = View.VISIBLE
            }
            is PlaylistsState.Error -> {
                binding.playlistsRecyclerView.visibility = View.GONE
                binding.playlistsEmptyView.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}
