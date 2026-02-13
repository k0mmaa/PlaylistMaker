package com.example.playlistmaker


import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.appbar.MaterialToolbar
import androidx.core.net.toUri


class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.parent_layout_settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBackMainActivity = findViewById<MaterialToolbar>(R.id.tool_bar)
        val btnSharedApp = findViewById<LinearLayout>(R.id.liner_icon_shared_app)
        val btnEmailSupport = findViewById<LinearLayout>(R.id.liner_icon_support)
        val btnUserAgreement = findViewById<LinearLayout>(R.id.liner_icon_agreement)

        btnBackMainActivity.setOnClickListener {
            finish()
        }

        btnSharedApp.setOnClickListener {
            val message = getString(R.string.url_address_curs)
            val sharedAppIntent = Intent(ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
                startActivity(Intent.createChooser(sharedAppIntent, getString(R.string.send_email)))
            }

        btnEmailSupport.setOnClickListener {
            val email = getString(R.string.email)
            val subject = getString(R.string.subject)
            val text = getString(R.string.text)

            val emailSupportIntent = Intent(ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, text)
            }
            startActivity(Intent.createChooser(emailSupportIntent, getString(R.string.send_email)))
        }

        btnUserAgreement.setOnClickListener {
            val url = getString(R.string.agreement_url)
            val webpage: Uri = url.toUri()
            val userAgreementIntent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(Intent.createChooser(userAgreementIntent,getString(R.string.read_agreemen)))
        }

    }
}