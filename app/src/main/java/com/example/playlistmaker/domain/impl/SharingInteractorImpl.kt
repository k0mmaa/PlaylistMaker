package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.ExternalNavigator
import com.example.playlistmaker.domain.api.SharingInteractor
import com.example.playlistmaker.domain.api.SharingRepository

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val sharingRepository: SharingRepository,
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun contactSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return sharingRepository.getShareAppLink()
    }

    private fun getTermsLink(): String {
        return sharingRepository.getTermsLink()
    }

    private fun getSupportEmailData(): com.example.playlistmaker.domain.models.EmailData {
        return sharingRepository.getSupportEmailData()
    }
}
