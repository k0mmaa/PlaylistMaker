package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
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
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private val viewModel by viewModel<SearchViewModel>()

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
        private const val KEY_NAME = "user_data"
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

        initViews()
        setupAdapter()

        viewModel.observeState().observe(this) { state ->
            render(state)
        }

        if (savedInstanceState != null) {
            inputEditText.setText(savedInstanceState.getString(KEY_NAME))
        }

        findViewById<MaterialToolbar>(R.id.tool_bar).setNavigationOnClickListener { finish() }

        // Устанавливаем начальное состояние крестика
        textInputLayout.isEndIconVisible = !inputEditText.text.isNullOrEmpty()

        inputEditText.addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                textInputLayout.isEndIconVisible = !s.isNullOrEmpty()
            },
            afterTextChanged = { s ->
                if (s.isNullOrEmpty()) {
                    viewModel.showHistory()
                } else {
                    viewModel.searchDebounce(s.toString())
                }
            }
        )

        textInputLayout.setEndIconOnClickListener {
            inputEditText.setText("")
            hideKeyboard()
            viewModel.showHistory()
        }

        refreshButton.setOnClickListener {
            viewModel.searchDebounce(inputEditText.text.toString())
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchDebounce(inputEditText.text.toString())
                true
            } else false
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isNullOrEmpty()) {
                viewModel.showHistory()
            }
        }

        btnCleanHistory.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    override fun onResume() {
        super.onResume()
        // Обновляем историю при каждом возврате на экран
        if (inputEditText.text.isNullOrEmpty()) {
            viewModel.showHistory()
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
    }

    private fun setupAdapter() {
        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            if (viewModel.clickDebounce()) {
                viewModel.addTrackToHistory(track)
                val intent = Intent(this, PlayerActivity::class.java).apply {
                    putExtra(PlayerActivity.INPUT_TRACK, track)
                }
                startActivity(intent)
            }
        }
        trackListRecyclerView.layoutManager = LinearLayoutManager(this)
        trackListRecyclerView.adapter = trackAdapter
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
        trackAdapter.updateTracks(tracks)
        trackListRecyclerView.visibility = View.VISIBLE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
        historyPlaceHolder.visibility = View.GONE
        btnCleanHistory.visibility = View.GONE
    }

    private fun showHistory(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        trackAdapter.updateTracks(tracks)
        trackListRecyclerView.visibility = View.VISIBLE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
        historyPlaceHolder.visibility = View.VISIBLE
        btnCleanHistory.visibility = View.VISIBLE
    }

    private fun showEmpty() {
        progressBar.visibility = View.GONE
        trackListRecyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.VISIBLE
        errorPlaceholder.visibility = View.GONE
        historyPlaceHolder.visibility = View.GONE
        btnCleanHistory.visibility = View.GONE
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        trackListRecyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.VISIBLE
        errorMessage.text = message
        historyPlaceHolder.visibility = View.GONE
        btnCleanHistory.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_NAME, inputEditText.text.toString())
    }
}
