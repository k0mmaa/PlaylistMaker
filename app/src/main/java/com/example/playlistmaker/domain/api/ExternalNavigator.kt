package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.EmailData

interface ExternalNavigator {
    fun shareLink(shareAppLink: String)
    fun openLink(termsLink: String)
    fun openEmail(supportEmailData: EmailData)
}
