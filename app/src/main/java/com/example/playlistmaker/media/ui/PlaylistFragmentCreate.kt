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

open class PlaylistFragmentCreate : Fragment() {

    open val viewModel by viewModel<PlaylistViewModelCreate>()

    protected var imageUri: Uri? = null
    protected var isImageSelected = false
    private var _binding: FragmentCreatePlaylistBinding? = null
    protected val binding get() = _binding!!

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageUri = uri
            binding.backgroundImageView.setImageURI(uri)
            binding.backgroundImageView.scaleType = ImageView.ScaleType.CENTER_CROP
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
            handleBackPressed()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPressed()
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
                onPlaylistSaved()
            }
        }

        binding.buttonCreatePlaylist.setOnClickListener {
            val name = binding.txtNamePlaylist.editText?.text.toString()
            val description = binding.txtDescriptionPlaylist.editText?.text.toString()
            viewModel.savePlaylist(name, description, imageUri)
        }
    }

    protected open fun handleBackPressed() {
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

    protected open fun onPlaylistSaved() {
        val name = binding.txtNamePlaylist.editText?.text.toString()
        Toast.makeText(requireContext(), getString(R.string.playlist_created, name), Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
