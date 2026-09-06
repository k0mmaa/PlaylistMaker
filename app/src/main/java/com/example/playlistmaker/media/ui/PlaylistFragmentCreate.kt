package com.example.playlistmaker.media.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragmentCreate : Fragment() {

    private val viewModel by viewModel<PlaylistViewModelCreate>()

    private var imageUri: Uri? = null
    private var isImageSelected = false
    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageUri = uri
            // 1. показываем выбранное фото
            binding.backgroundImageView.setImageURI(uri)
            // 2. Меняем масштаб на CENTER_CROP
            binding.backgroundImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            // 3. Меняем visibility на ImageView в true
            isImageSelected = true
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarCreatePlaylist.setNavigationOnClickListener {
            showExitConfirmationDialog()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        binding.coverCard.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.txtNamePlaylist.editText?.doOnTextChanged { text, start, before, count ->
            binding.buttonCreatePlaylist.isEnabled = text?.isNotBlank() == true
        }

        viewModel.playlistCreated.observe(viewLifecycleOwner) { created ->
            if (created) {
                val name = binding.txtNamePlaylist.editText?.text.toString()
                Toast.makeText(requireContext(), getString(R.string.playlist_created, name), Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        binding.buttonCreatePlaylist.setOnClickListener {
            val name = binding.txtNamePlaylist.editText?.text.toString()
            val description = binding.txtDescriptionPlaylist.editText?.text.toString()
            viewModel.createPlaylist(name, description, imageUri)
        }
    }

    private fun showExitConfirmationDialog() {
        val name = binding.txtNamePlaylist.editText?.text.toString()
        val description = binding.txtDescriptionPlaylist.editText?.text.toString()
        
        if (name.isNotEmpty() || description.isNotEmpty() || isImageSelected) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.finish_creating_playlist)
                .setMessage(R.string.unsaved_data_lost)
                .setPositiveButton(R.string.finish) { _, _ ->
                    findNavController().popBackStack()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        } else {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
