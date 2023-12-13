package com.ohdodok.catchytape.feature.playlist

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.ohdodok.catchytape.core.domain.utils.throttleFirst
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.core.ui.PlaylistAdapter
import com.ohdodok.catchytape.core.ui.RootViewInsetsCallback
import com.ohdodok.catchytape.core.ui.clicksFlow
import com.ohdodok.catchytape.core.ui.cterror.toMessageId
import com.ohdodok.catchytape.feature.playlist.databinding.FragmentPlaylistsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PlaylistsFragment : BaseFragment<FragmentPlaylistsBinding>(R.layout.fragment_playlists),
    NewPlaylistDialog.NewPlaylistDialogListener {

    val viewModel: PlaylistsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root, RootViewInsetsCallback())
        binding.viewModel = viewModel
        binding.rvPlaylist.adapter = PlaylistAdapter()
        viewModel.fetchPlaylists()

        observeEvents()
        setupButton(NewPlaylistDialog())
    }

    private fun setupButton(newPlaylistDialog: NewPlaylistDialog) {
        binding.fabNewPlaylist.clicksFlow()
            .throttleFirst(500)
            .onEach { newPlaylistDialog.show(childFragmentManager, NewPlaylistDialog.TAG) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeEvents() {
        repeatOnStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is PlaylistsEvent.ShowMessage -> {
                        showMessage(event.error.toMessageId())
                    }

                    is PlaylistsEvent.NavigateToPlaylistDetail -> {
                        findNavController().navigateToPlaylistDetail(
                            event.playlist.id,
                            event.playlist.title,
                        )
                    }
                }
            }
        }
    }

    override fun onPositiveButtonClicked(dialog: DialogFragment, title: String) {
        viewModel.createPlaylist(title)
    }
}

private fun NavController.navigateToPlaylistDetail(playlistId: Int, title: String) {
    navigate(
        PlaylistsFragmentDirections.actionPlaylistsFragmentToPlaylistDetailFragment(
            playlistId = playlistId,
            title = title,
        )
    )
}