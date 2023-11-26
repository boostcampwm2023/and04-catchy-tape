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
import timber.log.Timber

@AndroidEntryPoint
class PlayerFragment : BaseFragment<FragmentPlayerBinding>(R.layout.fragment_player) {
    private val viewModel: PlayerViewModel by viewModels()
    private lateinit var player: Player

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
        player = ExoPlayer.Builder(requireContext()).build()
        player.addListener(PlayerListener(viewModel))

        player.prepare()
    }

    private fun setMedia(url: String) {
        val mediaItem = MediaItem.fromUri(url)

        player.setMediaItem(mediaItem)
        player.play()
    }

    private fun setupButtons() {
        binding.btnPlay.setOnClickListener {
            player.play()
        }

        binding.btnPause.setOnClickListener {
            player.pause()
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