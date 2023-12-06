package com.ohdodok.catchytape.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ohdodok.catchytape.core.ui.databinding.BottomSheetPlaylistBinding
import com.ohdodok.catchytape.core.ui.model.PlaylistUiModel

class PlaylistBottomSheet(
    private val playlists: List<PlaylistUiModel>,
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        _binding = BottomSheetPlaylistBinding.inflate(inflater, container, false)

        binding.rvPlaylists.adapter = PlaylistAdapter().apply {
            submitList(playlists)
        }

        return binding.root
    }

    companion object {
        const val TAG = "PlaylistBottomSheet"
    }
}