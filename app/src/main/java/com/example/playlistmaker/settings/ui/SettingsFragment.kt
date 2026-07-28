package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.main.App
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Подписываемся на состояние темы из ViewModel
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            binding.darkModeSwitch.isChecked = state.themeSettings.isDarkTheme
        }

        binding.darkModeSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
            (requireContext().applicationContext as App).switchTheme(checked)
        }

        binding.linerIconSharedApp.setOnClickListener {
            viewModel.shareApp()
        }

        binding.linerIconSupport.setOnClickListener {
            viewModel.contactSupport()
        }

        binding.linerIconAgreement.setOnClickListener {
            viewModel.openTerms()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
