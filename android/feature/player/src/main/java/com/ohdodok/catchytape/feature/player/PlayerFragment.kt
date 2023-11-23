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

@AndroidEntryPoint
class PlayerFragment : BaseFragment<FragmentPlayerBinding>(R.layout.fragment_player) {
    private val viewModel: PlayerViewModel by viewModels()
    private lateinit var player: Player

    // todo : 실제 데이터로 변경
    private val url =
        "https://kr.object.ncloudstorage.com/catchy-tape-bucket2/transform/transform/test/music.m3u8"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        setupBackStack(binding.tbPlayer)

        setupPlayer()
        collectEvents()
    }

    private fun setupPlayer() {
        val mediaItem = MediaItem.fromUri(url)

        player = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = player

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    private fun collectEvents() {
        repeatOnStarted {
            viewModel.events.collect { event ->
                when (event) {
                    PlayerEvent.Play -> player.play()
                    PlayerEvent.Pause -> player.pause()
                }
            }
        }
    }
}