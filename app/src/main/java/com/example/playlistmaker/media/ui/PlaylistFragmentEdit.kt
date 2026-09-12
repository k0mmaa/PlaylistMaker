package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistFragmentEdit : PlaylistFragmentCreate() {

    private val args: PlaylistFragmentEditArgs by navArgs()

    override val viewModel: PlaylistViewModelEdit by viewModel {
        parametersOf(args.playlist)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlist = args.playlist
        
        binding.toolbarCreatePlaylist.title = getString(R.string.edit)
        binding.buttonCreatePlaylist.text = getString(R.string.save)

        binding.txtNamePlaylist.editText?.setText(playlist.name)
        binding.txtDescriptionPlaylist.editText?.setText(playlist.description)

        if (playlist.imagePath.isNotEmpty()) {
            Glide.with(this)
                .load(File(playlist.imagePath))
                .transform(CenterCrop())
                .into(binding.backgroundImageView)
            binding.backgroundImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            isImageSelected = true
        }
    }

    override fun handleBackPressed() {
        findNavController().popBackStack()
    }

    override fun onPlaylistSaved() {
        findNavController().popBackStack()
    }
}
