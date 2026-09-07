package com.example.playlistmaker.di

import com.example.playlistmaker.media.data.repository.FavoritesRepositoryImpl
import com.example.playlistmaker.media.domain.api.FavoritesRepository
import com.example.playlistmaker.player.data.AudioPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.AudioPlayerRepository
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get ())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(),get (),get ())
    }

    // Изменено на factory, чтобы каждый раз создавался новый плеер
    factory<AudioPlayerRepository> {
        AudioPlayerRepositoryImpl()
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(get())
    }

    single<SharingRepository> {
        SharingRepositoryImpl(get())
    }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(),get ())
    }

    single<com.example.playlistmaker.media.domain.api.PlaylistRepository> {
        com.example.playlistmaker.media.data.repository.PlaylistRepositoryImpl(get(), get(), get(), get(), get())
    }

}
