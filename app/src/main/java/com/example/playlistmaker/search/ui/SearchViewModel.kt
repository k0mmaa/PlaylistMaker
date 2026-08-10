package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var lastSearchText: String? = null
    private var searchJob: Job? = null
    private var isClickAllowed = true

    fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText) {
            return
        }

        this.lastSearchText = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(changedText)
        }
    }

    fun search(newSearchText: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchRequest(newSearchText)
        }
    }

    private suspend fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            tracksInteractor
                .searchTracks(newSearchText)
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val tracks = resource.data ?: emptyList()
                            if (tracks.isEmpty()) {
                                renderState(SearchState.Empty)
                            } else {
                                renderState(SearchState.Content(tracks))
                            }
                        }
                        is Resource.Error -> {
                            renderState(SearchState.Error(resource.message ?: "Ошибка"))
                        }
                    }
                }
        }
    }

    fun showHistory() {
        lastSearchText = null
        val history = searchHistoryInteractor.getHistory()
        if (history.isNotEmpty()) {
            renderState(SearchState.History(history))
        } else {
            renderState(SearchState.Content(emptyList()))
        }
    }

    fun clearHistory() {
        lastSearchText = null
        searchHistoryInteractor.clearHistory()
        renderState(SearchState.Content(emptyList()))
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrack(track)
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }
}
