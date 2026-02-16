package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SearchActivity : AppCompatActivity() {
    private lateinit var inputEditText: TextInputEditText
    private lateinit var textInputLayout: TextInputLayout

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

        // Инициализация объектов
        val btnToMainActivity = findViewById<MaterialToolbar>(R.id.tool_bar)
        textInputLayout = findViewById(R.id.search_input_layout)
        inputEditText = findViewById(R.id.input_strings)

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