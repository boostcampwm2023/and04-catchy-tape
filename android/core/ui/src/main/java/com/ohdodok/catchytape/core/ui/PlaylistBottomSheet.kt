package com.ohdodok.catchytape.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ohdodok.catchytape.core.ui.databinding.BottomSheetPlaylistBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistBottomSheet : BottomSheetDialogFragment() {
    val viewModel: PlaylistBottomSheetViewModel by viewModels()

    private var _binding: BottomSheetPlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPlaylistBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.rvPlaylists.adapter = PlaylistAdapter()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}