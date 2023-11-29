package com.ohdodok.catchytape.feature.player

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.feature.player.databinding.FragmentPlayerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject

const val millisecondsPerSecond = 1000

@AndroidEntryPoint
class PlayerFragment : BaseFragment<FragmentPlayerBinding>(R.layout.fragment_player) {

    @Inject
    lateinit var player: ExoPlayer
    private val viewModel: PlayerViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        setupPlayer()
        setUpSeekBar()
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
                if (player.isPlaying) {
                    val positionMs = player.currentPosition.toInt()
                    viewModel.updateCurrentPosition(positionMs / millisecondsPerSecond)
                }
            }
        }
    }

    private fun setUpSeekBar() {
        binding.sbMusicProgress.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player.seekTo(progress.toLong() * millisecondsPerSecond)
                    viewModel.updateCurrentPosition(progress)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
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

        binding.ibDown.setOnClickListener {
            findNavController().popBackStack()
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

fun NavController.navigateToPlayer() {
    this.navigate(R.id.player_nav_graph)
}
