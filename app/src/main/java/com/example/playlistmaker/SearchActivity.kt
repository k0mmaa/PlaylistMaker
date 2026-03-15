package com.example.playlistmaker


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
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
    private lateinit var errorMessage: TextView

    companion object {
        private val apiBaseUrl = "https://itunes.apple.com"
        private const val KEY_NAME = "user_data"
        val trackList = mutableListOf<Track>()
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
        textInputLayout = findViewById(R.id.search_input_layout)
        inputEditText = findViewById(R.id.input_strings)
        trackListRecyclerView = findViewById(R.id.trackRecyclerView)
        emptyPlaceholder = findViewById(R.id.emptyPlaceholder)
        errorPlaceholder = findViewById(R.id.errorPlaceholder)
        refreshButton = findViewById(R.id.refreshButton)
        errorMessage = findViewById(R.id.errorMessage)



        trackAdapter = TrackAdapter(trackList)
        trackListRecyclerView.layoutManager = LinearLayoutManager(this)
        trackListRecyclerView.adapter = trackAdapter


        // Восстановление текста - если есть
        if (savedInstanceState != null) {
            inputEditText.setText(savedInstanceState.getString(KEY_NAME))
        }

        // Обработка нажатия на стрелку назад
        btnToMainActivity.setOnClickListener {
            finish()
        }

        //
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Не требуется
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // удалил код, т.к. добавил крестик из Material Design, знаю, что не по теории, но так оказалось проще
                //раньше было так. Если необходимо, могу вернуть код - он был в пулреквесте №2
                /*if (s!!.isNotEmpty()) {
                    btnCleanStringSearch.visibility = clearButtonVisibility(s)*/
            }

            override fun afterTextChanged(s: Editable?) {
                // Не требуется
            }
        })

        refreshButton.setOnClickListener {
            val query = inputEditText.text.toString()
            if (query.isNotBlank())
                searchTracks(query)
        }

        //inputEditText.addTextChangedListener(simpleTextWatcher) - т.к. использовал знак крест из Material Design

        //обработчик нажатии кнопки Done
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // поисковый ЗАПРОС 0J/RgNC40LLQtdGCIdCl0L7RgNC+0YjQtdCz0L4g0LTQvdGPCg==
                val query = inputEditText.text.toString()
                if (query.isNotBlank()) {
                    searchTracks(query)
                }
                true
            } else {
                false
            }
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

        trackApiService.searchTrack(query).enqueue(object : Callback<TrackResponse> {

            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                //успешный ответ (код 200)
                when (response.code()) {
                    200 -> handleSearchResponse(response.body())
                    else -> showError("Ошибка сервера: ${response.code()}")
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

    fun showTracks(){
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

}



