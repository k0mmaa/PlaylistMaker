package com.example.playlistmaker.sharing.data

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.api.SharingRepository
import com.example.playlistmaker.sharing.domain.models.EmailData

class SharingRepositoryImpl(private val context: Context) : SharingRepository {
    override fun getShareAppLink(): String {
        return context.getString(R.string.url_address_curs)
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.agreement_url)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            email = context.getString(R.string.email),
            subject = context.getString(R.string.subject),
            text = context.getString(R.string.text)
        )
    }
}
