package com.ohdodok.catchytape.feature.player

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.feature.player.databinding.FragmentPlayerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PlayerFragment : BaseFragment<FragmentPlayerBinding>(R.layout.fragment_player) {

    private val viewModel: PlayerViewModel by viewModels()
    @Inject lateinit var player: ExoPlayer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        setupBackStack(binding.tbPlayer)

        setupPlayer()
        // todo : 실제 데이터로 변경
        setMedia("https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8")
        setupButtons()
        collectEvents()
    }

    private fun setupPlayer() {
        player.addListener(PlayerListener(viewModel))

        player.prepare()
        setupPlayerTimer()
    }

    private fun setupPlayerTimer() {
        repeatOnStarted {
            while (true) {
                delay(1000L)
                val positionMs = player.currentPosition.toInt()
                viewModel.updateCurrentPosition(positionMs / 1000)
            }
        }
    }

    private fun setMedia(url: String) {
        val mediaItem = MediaItem.fromUri(url)

        player.setMediaItem(mediaItem)
        player.play()
    }

    private fun setupButtons() {
        binding.btnPlay.setOnClickListener {
            if (viewModel.uiState.value.isPlaying) player.pause()
            else player.play()
        }
    }

    private fun collectEvents() {
        repeatOnStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is PlayerEvent.ShowError -> Timber.d(event.error.message ?: "")
                }
            }
        }
    }
}