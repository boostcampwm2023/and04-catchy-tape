package com.ohdodok.catchytape.feature.playlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.core.ui.toMessageId
import com.ohdodok.catchytape.feature.playlist.databinding.FragmentPlaylistsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistsFragment : BaseFragment<FragmentPlaylistsBinding>(R.layout.fragment_playlists),
    NewPlaylistDialog.NewPlaylistDialogListener {

    val viewModel: PlaylistViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.rvPlaylist.adapter = PlaylistAdapter()
        viewModel.fetchPlaylists()

        observeEvents()
        binding.fabNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_playlist_fragment_to_newPlaylistDialog)
        }

    }


    private fun observeEvents() {
        repeatOnStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is PlaylistsEvent.ShowMessage -> {
                        showMessage(event.error.toMessageId())
                    }
                }
            }
        }
    }

    override fun onPositiveButtonClicked(dialog: DialogFragment, title: String) {
        viewModel.createPlaylist(title)
    }
}