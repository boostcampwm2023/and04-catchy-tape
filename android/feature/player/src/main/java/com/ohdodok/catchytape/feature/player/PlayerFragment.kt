package com.ohdodok.catchytape.feature.player

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.core.ui.PlaylistBottomSheet
import com.ohdodok.catchytape.core.ui.toMessageId
import com.ohdodok.catchytape.feature.player.databinding.FragmentPlayerBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val millisecondsPerSecond = 1000

@AndroidEntryPoint
class PlayerFragment : BaseFragment<FragmentPlayerBinding>(R.layout.fragment_player) {

    @Inject
    lateinit var player: ExoPlayer
    private val viewModel: PlayerViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        setUpSeekBar()
        setupButtons()
        collectEvents()
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

    private fun setupButtons() {
        binding.btnPlay.setOnClickListener {
            if (viewModel.uiState.value.isPlaying) player.pause()
            else player.play()
        }

        binding.ibDown.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnNext.setOnClickListener {
            player.moveNextMedia()
        }

        binding.btnPrevious.setOnClickListener {
            player.movePreviousMedia()
        }

        binding.btnAddToPlaylist.setOnClickListener {
            val bottomSheet = PlaylistBottomSheet()
            bottomSheet.show(parentFragmentManager, PlaylistBottomSheet.TAG)
        }
    }

    private fun collectEvents() {
        repeatOnStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is PlayerEvent.ShowError -> showMessage(event.error.toMessageId())
                }
            }
        }
    }
}

fun NavController.navigateToPlayer() {
    this.navigate(R.id.player_nav_graph)
}
