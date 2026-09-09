package com.example.playlistmaker.di

import com.example.playlistmaker.media.ui.FavoritesViewModel
import com.example.playlistmaker.media.ui.MediaViewModel
import com.example.playlistmaker.media.ui.PlaylistViewModel
import com.example.playlistmaker.media.ui.PlaylistViewModelCreate
import com.example.playlistmaker.media.ui.PlaylistViewModelEdit
import com.example.playlistmaker.media.ui.PlaylistsViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import com.example.playlistmaker.media.domain.models.Playlist
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        PlayerViewModel(get(),get(),get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel { MediaViewModel() }
    viewModel { FavoritesViewModel(get()) }
    viewModel { PlaylistsViewModel(get()) }
    viewModel { PlaylistViewModelCreate(get()) }
    viewModel { (playlistId: Int) ->
        PlaylistViewModel(playlistId, get(), get())
    }
    viewModel { (playlist: Playlist) ->
        PlaylistViewModelEdit(playlist, get())
    }

}
