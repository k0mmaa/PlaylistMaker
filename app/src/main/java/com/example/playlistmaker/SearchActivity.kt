package com.example.playlistmaker


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {


    private val retrofit = Retrofit.Builder()
        .baseUrl(apiBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val trackApiService = retrofit.create(TrackApiService::class.java)

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var inputEditText: TextInputEditText
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var trackListRecyclerView: RecyclerView
    private lateinit var emptyPlaceholder: LinearLayout
    private lateinit var errorPlaceholder: LinearLayout
    private lateinit var refreshButton: Button
    private lateinit var btnCleanHistory: Button
    private lateinit var errorMessage: TextView
    private lateinit var searchHistory: SearchHistory
    private lateinit var historyPlaceHolder: TextView


    companion object {
        private val apiBaseUrl = "https://itunes.apple.com"
        private const val KEY_NAME = "user_data"
        val trackList = mutableListOf<Track>()
        const val TRACK_HISTORY_PREF = "track_history_preferences"
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

        // Инициализация объектов
        val btnToMainActivity = findViewById<MaterialToolbar>(R.id.tool_bar)
        val sharedPrefs = getSharedPreferences(TRACK_HISTORY_PREF, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)
        btnCleanHistory = findViewById<Button>(R.id.clearHistoryButton)
        historyPlaceHolder = findViewById(R.id.tv_search_history)
        textInputLayout = findViewById(R.id.search_input_layout)
        inputEditText = findViewById(R.id.input_strings)
        trackListRecyclerView = findViewById(R.id.trackRecyclerView)
        emptyPlaceholder = findViewById(R.id.emptyPlaceholder)
        errorPlaceholder = findViewById(R.id.errorPlaceholder)
        refreshButton = findViewById(R.id.refreshButton)
        errorMessage = findViewById(R.id.errorMessage)


        //скрываем кнопку очистки истории поиска
        historyPlaceHolder.visibility = View.GONE
        btnCleanHistory.visibility = View.GONE




        trackAdapter = TrackAdapter(trackList) { track ->
            searchHistory.addTrack(track)
            if (inputEditText.text.isNullOrEmpty()) {
                trackAdapter.updateTracks(searchHistory.getHistory())
            }
        }
        trackListRecyclerView.layoutManager = LinearLayoutManager(this)
        trackListRecyclerView.adapter = trackAdapter


        // Восстановление текста - если есть
        if (savedInstanceState != null) {
            inputEditText.setText(savedInstanceState.getString(KEY_NAME))
        }

        // Обработка нажатия на стрелку назад
        btnToMainActivity.setOnClickListener { finish() }

        //TextWatcher
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // удалил код, т.к. добавил крестик из Material Design, знаю, что не по теории, но так оказалось проще
                //раньше было так. Если необходимо, могу вернуть код - он был в пулреквесте №2
                /*if (s!!.isNotEmpty()) {
                    btnCleanStringSearch.visibility = clearButtonVisibility(s)*/
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    val history = searchHistory.getHistory()
                    if (history.isNotEmpty()) {
                        showHistory(history)
                    } else {
                        hideHistory()
                        clearTracklist()
                    }
                }else{
                    hideHistory()
                    trackListRecyclerView.visibility = View.GONE
                }
            }
        })


        refreshButton.setOnClickListener {
            val query = inputEditText.text.toString()
            if (query.isNotBlank())
                searchTracks(query)
        }
        //обработчик нажатии кнопки Done
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // 0JTQsCDQv9GA0LjQsdGD0LTQtdGCINGBINGC0L7QsdC+0Lkg0YHQuNC70LAhINCS0YvRh9C40YLQsNGC0Ywg0Y3RgtC+0YIg0LHQtdC30YPQvNC90YvQuSDQutC+0LQhCg==
                val query = inputEditText.text.toString()
                if (query.isNotBlank()) searchTracks(query)
                true
            } else false
        }

        //реализация фокуса
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            val history = searchHistory.getHistory()
            if (hasFocus && inputEditText.text.isNullOrEmpty() && history.isNotEmpty()) {
                showHistory(history)
            } else {
                hideHistory()
            }
        }

        btnCleanHistory.setOnClickListener {
            searchHistory.clearHistory()
            trackAdapter.updateTracks(emptyList())
            historyPlaceHolder.visibility = View.GONE
            btnCleanHistory.visibility = View.GONE
            clearTracklist()
        }
    }


    // Сохраняем  данные пользователя
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_NAME, inputEditText.text.toString())
    }

    // Восстанавливаем состояние/данные пользователя
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputEditText.setText(savedInstanceState.getString(KEY_NAME))
    }


    private fun searchTracks(query: String) {
        hideHistory()

        trackApiService.searchTrack(query).enqueue(object : Callback<TrackResponse> {

            override fun onResponse(
                call: Call<TrackResponse>,
                response: Response<TrackResponse>
            ) {
                if (response.isSuccessful) {
                    handleSearchResponse(response.body())
                } else {
                    showError("Ошибка сервера: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                // ошибка сети/сервера
                showError(getString(R.string.network_error))
            }
        })
    }

    private fun handleSearchResponse(response: TrackResponse?) {
        if (response == null) return

        if (response.resultCount > 0) {
            // Есть треки - обновляем адаптер и показываем список
            trackAdapter.updateTracks(response.results)
            showTracks()
        } else {
            // Нет треков - показываем заглушку
            showEmpty()
        }
    }

    private fun showEmpty() {
        trackListRecyclerView.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
        emptyPlaceholder.visibility = View.VISIBLE
    }

    fun showTracks() {
        //отрисовываем ресайкл
        trackListRecyclerView.visibility = View.VISIBLE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE

    }

    private fun showError(message: String) {
        // Скрываем список
        trackListRecyclerView.visibility = View.GONE
        errorPlaceholder.visibility = View.VISIBLE
        emptyPlaceholder.visibility = View.GONE
        errorMessage.text = message
    }

    private fun clearTracklist() {
        trackAdapter.updateTracks(emptyList())
        hideKeyboard()
        trackListRecyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE

    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
}






