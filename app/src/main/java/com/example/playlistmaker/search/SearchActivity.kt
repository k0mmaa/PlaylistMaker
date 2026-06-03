package com.example.playlistmaker.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.player.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchState
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.search.ui.SearchViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel

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

    companion object {
        const val KEY_NAME = "KEY_NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.parent_layout_search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this, SearchViewModelFactory(this))[SearchViewModel::class.java]

        initViews()
        setupListeners()
        setupAdapter()

        viewModel.observeState().observe(this) {
            render(it)
        }

        if (savedInstanceState != null) {
            inputEditText.setText(savedInstanceState.getString(KEY_NAME))
        }
    }

    private fun initViews() {
        val toolbar = findViewById<MaterialToolbar>(R.id.tool_bar)
        toolbar.setNavigationOnClickListener { finish() }

        inputEditText = findViewById(R.id.input_strings)
        textInputLayout = findViewById(R.id.search_input_layout)
        trackListRecyclerView = findViewById(R.id.trackRecyclerView)
        emptyPlaceholder = findViewById(R.id.emptyPlaceholder)
        errorPlaceholder = findViewById(R.id.errorPlaceholder)
        refreshButton = findViewById(R.id.refreshButton)
        btnCleanHistory = findViewById(R.id.clearHistoryButton)
        errorMessage = findViewById(R.id.errorMessage)
        historyPlaceHolder = findViewById(R.id.tv_search_history)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        refreshButton.setOnClickListener {
            viewModel.searchDebounce(inputEditText.text.toString())
        }

        btnCleanHistory.setOnClickListener {
            viewModel.clearHistory()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    textInputLayout.endIconMode = TextInputLayout.END_ICON_NONE
                    if (inputEditText.hasFocus()) {
                        viewModel.showHistory()
                    }
                } else {
                    textInputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
                    viewModel.searchDebounce(s.toString())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        textInputLayout.setEndIconOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            viewModel.showHistory()
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text?.isNotEmpty() == true) {
                    viewModel.searchDebounce(inputEditText.text.toString())
                }
                true
            } else false
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isNullOrEmpty()) {
                viewModel.showHistory()
            }
        }
    }

    private fun setupAdapter() {
        trackAdapter = TrackAdapter(emptyList()) { track ->
            viewModel.addTrackToHistory(track)
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("track_json", Gson().toJson(track))
            }
            startActivity(intent)
        }
        trackListRecyclerView.layoutManager = LinearLayoutManager(this)
        trackListRecyclerView.adapter = trackAdapter
    }

    override fun onResume() {
        super.onResume()
        if (inputEditText.text.isNullOrEmpty()) {
            viewModel.showHistory()
        }
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
        progressBar.visibility = View.VISIBLE
        trackListRecyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
        historyPlaceHolder.visibility = View.GONE
        btnCleanHistory.visibility = View.GONE
    }

    private fun showContent(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        trackListRecyclerView.visibility = View.VISIBLE
        trackAdapter.updateTracks(tracks)
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
        historyPlaceHolder.visibility = View.GONE
        btnCleanHistory.visibility = View.GONE
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        trackListRecyclerView.visibility = View.GONE
        errorPlaceholder.visibility = View.VISIBLE
        errorMessage.text = message
        emptyPlaceholder.visibility = View.GONE
        historyPlaceHolder.visibility = View.GONE
        btnCleanHistory.visibility = View.GONE
    }

    private fun showEmpty() {
        progressBar.visibility = View.GONE
        trackListRecyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.VISIBLE
        errorPlaceholder.visibility = View.GONE
        historyPlaceHolder.visibility = View.GONE
        btnCleanHistory.visibility = View.GONE
    }

    private fun showHistory(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        trackListRecyclerView.visibility = View.VISIBLE
        trackAdapter.updateTracks(tracks)
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
        historyPlaceHolder.visibility = View.VISIBLE
        btnCleanHistory.visibility = View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_NAME, inputEditText.text.toString())
    }
}
