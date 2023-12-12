package com.ohdodok.catchytape.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ohdodok.catchytape.core.ui.cterror.toMessageId
import com.ohdodok.catchytape.core.ui.databinding.BottomSheetPlaylistBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

        observeEvents()

        return binding.root
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is PlaylistBottomSheetEvent.Success -> {
                            showMessage(R.string.add_to_playlist_success)
                            findNavController().popBackStack()
                        }

                        is PlaylistBottomSheetEvent.ShowMessage -> {
                            showMessage(event.error.toMessageId())
                        }
                    }
                }
            }
        }
    }

    private fun showMessage(@StringRes messageId: Int) {
        Toast.makeText(requireActivity(), messageId, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}