package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SearchActivity : AppCompatActivity() {

    private val tracksInteractor = Creator.provideTracksInteractor()
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor

    private val searchRunnable = Runnable { searchTracks(inputEditText.text.toString()) }

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var inputEditText: TextInputEditText
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var trackListRecyclerView: RecyclerView
    private lateinit var emptyPlaceholder: LinearLayout
    private lateinit var errorPlaceholder: LinearLayout
    private lateinit var refreshButton: Button
    private lateinit var btnCleanHistory: Button
    private lateinit var errorMessage: TextView
    private lateinit var historyPlaceHolder: TextView
    private lateinit var progressBar: ProgressBar

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val trackList = mutableListOf<Track>()

    companion object {
        private const val KEY_NAME = "user_data"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.parent_layout_search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)

        initViews()
        setupAdapter()

        if (savedInstanceState != null) {
            inputEditText.setText(savedInstanceState.getString(KEY_NAME))
        }

        findViewById<MaterialToolbar>(R.id.tool_bar).setNavigationOnClickListener { finish() }

        inputEditText.addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                textInputLayout.isEndIconVisible = !s.isNullOrEmpty()
            },
            afterTextChanged = { s ->
                if (s.isNullOrEmpty()) {
                    handler.removeCallbacks(searchRunnable)
                    clearTracklist()
                    val history = searchHistoryInteractor.getHistory()
                    if (history.isNotEmpty()) {
                        showHistory(history)
                    } else {
                        hideHistory()
                    }
                } else {
                    hideHistory()
                    trackListRecyclerView.visibility = View.GONE
                    searchDebounce()
                }
            }
        )

        textInputLayout.setEndIconOnClickListener {
            inputEditText.setText("")
            hideKeyboard()
            val history = searchHistoryInteractor.getHistory()
            if (history.isNotEmpty()) {
                showHistory(history)
            } else {
                hideHistory()
                clearTracklist()
            }
        }

        refreshButton.setOnClickListener {
            val query = inputEditText.text.toString()
            if (query.isNotBlank() && clickDebounce()) {
                searchTracks(query)
            }
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = inputEditText.text.toString()
                if (query.isNotBlank() && clickDebounce()) {
                    searchTracks(query)
                }
                true
            } else false
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            val history = searchHistoryInteractor.getHistory()
            if (hasFocus && inputEditText.text.isNullOrEmpty() && history.isNotEmpty()) {
                showHistory(history)
            } else {
                hideHistory()
            }
        }

        btnCleanHistory.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            trackAdapter.updateTracks(emptyList())
            historyPlaceHolder.visibility = View.GONE
            btnCleanHistory.visibility = View.GONE
            clearTracklist()
        }

        val currentHistory = searchHistoryInteractor.getHistory()
        if (inputEditText.text.isNullOrEmpty() && currentHistory.isNotEmpty()) {
            showHistory(currentHistory)
        }
    }

    private fun initViews() {
        btnCleanHistory = findViewById(R.id.clearHistoryButton)
        historyPlaceHolder = findViewById(R.id.tv_search_history)
        textInputLayout = findViewById(R.id.search_input_layout)
        inputEditText = findViewById(R.id.input_strings)
        trackListRecyclerView = findViewById(R.id.trackRecyclerView)
        emptyPlaceholder = findViewById(R.id.emptyPlaceholder)
        errorPlaceholder = findViewById(R.id.errorPlaceholder)
        refreshButton = findViewById(R.id.refreshButton)
        errorMessage = findViewById(R.id.errorMessage)
        progressBar = findViewById(R.id.progressBar)

        historyPlaceHolder.visibility = View.GONE
        btnCleanHistory.visibility = View.GONE
        textInputLayout.isEndIconVisible = !inputEditText.text.isNullOrEmpty()
    }

    private fun setupAdapter() {
        trackAdapter = TrackAdapter(trackList) { track ->
            if (clickDebounce()) {
                searchHistoryInteractor.addTrack(track)
                val intent = Intent(this, PlayerActivity::class.java).apply {
                    putExtra(PlayerActivity.INPUT_TRACK, track)
                }
                startActivity(intent)
                if (inputEditText.text.isNullOrEmpty()) {
                    trackAdapter.updateTracks(searchHistoryInteractor.getHistory())
                }
            }
        }
        trackListRecyclerView.layoutManager = LinearLayoutManager(this)
        trackListRecyclerView.adapter = trackAdapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_NAME, inputEditText.text.toString())
    }

    private fun searchTracks(query: String) {
        handler.removeCallbacks(searchRunnable)
        if (query.isNotEmpty()) {
            showLoading()
            hideHistory()

            tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    handler.post {
                        // КРИТИЧЕСКОЕ ИСПРАВЛЕНИЕ: Проверяем, актуален ли еще этот запрос
                        if (inputEditText.text.toString() == query) {
                            progressBar.visibility = View.GONE
                            if (foundTracks != null) {
                                trackAdapter.updateTracks(foundTracks)
                                showTracks()
                            }
                            if (errorMessage != null) {
                                showError(errorMessage)
                            } else if (foundTracks.isNullOrEmpty()) {
                                showEmpty()
                            }
                        }
                    }
                }
            })
        }
    }

    private fun showEmpty() {
        trackListRecyclerView.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
        emptyPlaceholder.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    fun showTracks() {
        trackListRecyclerView.visibility = View.VISIBLE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun showError(message: String) {
        trackListRecyclerView.visibility = View.GONE
        errorPlaceholder.visibility = View.VISIBLE
        emptyPlaceholder.visibility = View.GONE
        errorMessage.text = message
        progressBar.visibility = View.GONE
    }

    private fun clearTracklist() {
        trackAdapter.updateTracks(emptyList())
        hideKeyboard()
        trackListRecyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }

    private fun showHistory(history: List<Track>) {
        historyPlaceHolder.visibility = View.VISIBLE
        btnCleanHistory.visibility = View.VISIBLE
        trackAdapter.updateTracks(history)
        showTracks()
    }

    private fun hideHistory() {
        historyPlaceHolder.visibility = View.GONE
        btnCleanHistory.visibility = View.GONE
    }

    fun showLoading() {
        progressBar.visibility = View.VISIBLE
        trackListRecyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
        btnCleanHistory.visibility = View.GONE
        historyPlaceHolder.visibility = View.GONE
        refreshButton.visibility = View.GONE
        errorMessage.visibility = View.GONE
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }
}
