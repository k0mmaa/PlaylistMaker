package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

class SearchActivity : AppCompatActivity() {
    private lateinit var inputEditText: TextInputEditText
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var trackListRecyclerView: RecyclerView

    companion object {
        private const val KEY_NAME = "user_data"
            val trackList = mutableListOf<Track>()

            init {
                trackList.add(Track(
                    trackName = "Smells Like Teen Spirit",
                    artistName = "Nirvana",
                    trackTime = "5:01",
                    artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
                ))

                trackList.add(Track(
                    trackName = "Billie Jean",
                    artistName = "Michael Jackson",
                    trackTime = "4:35",
                    artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg",
                ))

                trackList.add(Track(
                    "Stayin' Alive",
                    "Bee Gees",
                    "4:10",
                    "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg",
                ))

                trackList.add(Track(
                    "Whole Lotta Love",
                    "Led Zeppelin",
                    "5:33",
                    "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg",
                ))

                trackList.add(Track(
                    "Sweet Child O'Mine",
                    "Guns N' Roses",
                    "5:03",
                    "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg",
                ))
            }
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


        trackListRecyclerView.layoutManager = LinearLayoutManager(this)
        trackListRecyclerView.adapter = TrackAdapter(trackList)






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

            //inputEditText.addTextChangedListener(simpleTextWatcher) - т.к. использовал знак крест из Material Design


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
}