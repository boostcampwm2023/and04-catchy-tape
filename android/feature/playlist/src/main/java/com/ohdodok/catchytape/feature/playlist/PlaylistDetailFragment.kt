package com.ohdodok.catchytape.feature.playlist

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.core.ui.MusicAdapter
import com.ohdodok.catchytape.core.ui.Orientation
import com.ohdodok.catchytape.core.ui.toMessageId
import com.ohdodok.catchytape.feature.playlist.databinding.FragmentPlaylistDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistDetailFragment :
    BaseFragment<FragmentPlaylistDetailBinding>(R.layout.fragment_playlist_detail) {

    private val viewModel: PlaylistDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        setupBackStack(binding.tbPlaylistDetail)

        setupPlaylist()
        collectEvents()
    }

    private fun setupPlaylist() {
        binding.rvPlaylist.adapter = MusicAdapter(
            musicItemOrientation = Orientation.VERTICAL,
            listener = viewModel,
        )
    }

    private fun collectEvents() {
        repeatOnStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is PlaylistDetailEvent.ShowMessage -> showMessage(event.error.toMessageId())
                    is PlaylistDetailEvent.NavigateToPlayerScreen -> findNavController().navigateToPlayerScreen()
                }
            }
        }
    }
}

private fun NavController.navigateToPlayerScreen() {
    val targetUri = "android-app://com.ohdodok.catchytape/player_fragment".toUri()
    val request = NavDeepLinkRequest.Builder.fromUri(targetUri).build()

    this.navigate(request)
}