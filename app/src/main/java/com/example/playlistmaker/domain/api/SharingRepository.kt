package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.EmailData

interface SharingRepository {
    fun getShareAppLink(): String
    fun getTermsLink(): String
    fun getSupportEmailData(): EmailData
}
