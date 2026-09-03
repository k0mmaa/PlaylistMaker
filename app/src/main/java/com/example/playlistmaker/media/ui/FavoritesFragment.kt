package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.domain.models.Track

class FavoritesFragment : Fragment() {

    private val viewModel by viewModel<FavoritesViewModel>()

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var trackAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observeState()
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun initRecyclerView() {
        trackAdapter = TrackAdapter(emptyList()) { track ->
            onTrackClick(track)
        }
        binding.favoritesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }
    }

    private fun onTrackClick(track: Track) {
        if (viewModel.clickDebounce()) {
            findNavController().navigate(
                R.id.action_mediaFragment_to_playerFragment,
                bundleOf(PlayerFragment.ARGS_TRACK to track)
            )
        }
    }

    private fun renderState(state: FavoritesState) {
        when (state) {
            is FavoritesState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.favoritesRecyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.GONE
            }
            is FavoritesState.Content -> {
                binding.progressBar.visibility = View.GONE
                binding.favoritesRecyclerView.visibility = View.VISIBLE
                binding.emptyView.visibility = View.GONE
                trackAdapter.updateTracks(state.tracks)
            }
            is FavoritesState.Empty -> {
                binding.progressBar.visibility = View.GONE
                binding.favoritesRecyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
            }
            is FavoritesState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.favoritesRecyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}
