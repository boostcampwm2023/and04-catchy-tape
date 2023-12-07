package com.ohdodok.catchytape.feature.home

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.core.ui.MusicAdapter
import com.ohdodok.catchytape.core.ui.Orientation
import com.ohdodok.catchytape.core.ui.RootViewInsetsCallback
import com.ohdodok.catchytape.core.ui.toMessageId
import com.ohdodok.catchytape.feature.home.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root, RootViewInsetsCallback())

        binding.viewModel = viewModel

        binding.rvRecentlyAddedSong.adapter = MusicAdapter(
            musicItemOrientation = Orientation.HORIZONTAL,
            listener = viewModel,
        )
        observeEvents()
        viewModel.fetchUploadedMusics()
        viewModel.fetchRecentPlayedMusics()
        setupButtons()
    }

    private fun observeEvents() {
        repeatOnStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is HomeEvent.ShowMessage -> {
                        showMessage(event.error.toMessageId())
                    }

                    is HomeEvent.NavigateToPlayerScreen -> {
                        findNavController().navigateToPlayerScreen()
                    }
                }
            }
        }
    }

    private fun setupButtons() {
        binding.ibUpload.setOnClickListener {
            val request =
                NavDeepLinkRequest.Builder.fromUri("android-app://com.ohdodok.catchytape/upload_fragment".toUri())
                    .build()
            findNavController().navigate(request)
        }

        binding.ivRecentlyPlayedSong.setOnClickListener {
            findNavController().navigateToPlayerScreen()
        }
    }
}

private fun NavController.navigateToPlayerScreen() {
    val request =
        NavDeepLinkRequest.Builder.fromUri("android-app://com.ohdodok.catchytape/player_fragment".toUri())
            .build()

    this.navigate(request)
}