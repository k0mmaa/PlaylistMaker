package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel by viewModel<SearchViewModel>()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var trackAdapter: TrackAdapter

    companion object {
        private const val KEY_NAME = "user_data"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        if (savedInstanceState != null) {
            binding.inputStrings.setText(savedInstanceState.getString(KEY_NAME))
        }

        // Устанавливаем начальное состояние крестика
        binding.searchInputLayout.isEndIconVisible = !binding.inputStrings.text.isNullOrEmpty()

        binding.inputStrings.addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                binding.searchInputLayout.isEndIconVisible = !s.isNullOrEmpty()
            },
            afterTextChanged = { s ->
                if (s.isNullOrEmpty()) {
                    viewModel.showHistory()
                } else {
                    viewModel.searchDebounce(s.toString())
                }
            }
        )

        binding.searchInputLayout.setEndIconOnClickListener {
            binding.inputStrings.setText("")
            hideKeyboard()
            viewModel.showHistory()
        }

        binding.refreshButton.setOnClickListener {
            viewModel.search(binding.inputStrings.text.toString())
        }

        binding.inputStrings.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.search(binding.inputStrings.text.toString())
                true
            } else false
        }

        binding.inputStrings.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.inputStrings.text.isNullOrEmpty()) {
                viewModel.showHistory()
            }
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.inputStrings.text.isNullOrEmpty()) {
            viewModel.showHistory()
        }
    }

    private fun setupAdapter() {
        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            if (viewModel.clickDebounce()) {
                viewModel.addTrackToHistory(track)
                findNavController().navigate(
                    R.id.action_searchFragment_to_playerFragment,
                    bundleOf(PlayerFragment.ARGS_TRACK to track)
                )
            }
        }
        binding.trackRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.trackRecyclerView.adapter = trackAdapter
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Error -> showError(state.message)
            is SearchState.Empty -> showEmpty()
            is SearchState.History -> showHistory(state.tracks)
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.trackRecyclerView.visibility = View.GONE
        binding.emptyPlaceholder.visibility = View.GONE
        binding.errorPlaceholder.visibility = View.GONE
        binding.tvSearchHistory.visibility = View.GONE
        binding.clearHistoryButton.visibility = View.GONE
    }

    private fun showContent(tracks: List<Track>) {
        binding.progressBar.visibility = View.GONE
        trackAdapter.updateTracks(tracks)
        binding.trackRecyclerView.visibility = View.VISIBLE
        binding.emptyPlaceholder.visibility = View.GONE
        binding.errorPlaceholder.visibility = View.GONE
        binding.tvSearchHistory.visibility = View.GONE
        binding.clearHistoryButton.visibility = View.GONE
    }

    private fun showHistory(tracks: List<Track>) {
        binding.progressBar.visibility = View.GONE
        trackAdapter.updateTracks(tracks)
        binding.trackRecyclerView.visibility = View.VISIBLE
        binding.emptyPlaceholder.visibility = View.GONE
        binding.errorPlaceholder.visibility = View.GONE
        binding.tvSearchHistory.visibility = View.VISIBLE
        binding.clearHistoryButton.visibility = View.VISIBLE
    }

    private fun showEmpty() {
        binding.progressBar.visibility = View.GONE
        binding.trackRecyclerView.visibility = View.GONE
        binding.emptyPlaceholder.visibility = View.VISIBLE
        binding.errorPlaceholder.visibility = View.GONE
        binding.tvSearchHistory.visibility = View.GONE
        binding.clearHistoryButton.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.trackRecyclerView.visibility = View.GONE
        binding.emptyPlaceholder.visibility = View.GONE
        binding.errorPlaceholder.visibility = View.VISIBLE
        binding.errorMessage.text = message
        binding.tvSearchHistory.visibility = View.GONE
        binding.clearHistoryButton.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.inputStrings.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (_binding != null) {
            outState.putString(KEY_NAME, binding.inputStrings.text.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
