package com.ohdodok.catchytape.feature.player

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.core.ui.RootViewInsetsCallback
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

        ViewCompat.setOnApplyWindowInsetsListener(binding.root, RootViewInsetsCallback())
        binding.viewModel = viewModel

        setUpSeekBar()
        setupButtons()
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
        binding.ibPlay.setOnClickListener {
            if (viewModel.uiState.value.isPlaying) player.pause()
            else player.play()
        }

        binding.ibDown.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ibNext.setOnClickListener {
            player.moveNextMedia()
        }

        binding.ibPrevious.setOnClickListener {
            player.onPreviousBtnClick()
        }

        binding.btnAddToPlaylist.setOnClickListener {
            findNavController().showPlaylistBottomSheet()
        }
    }

    private fun NavController.showPlaylistBottomSheet() {
        val musicId = viewModel.uiState.value.currentMusic?.id ?: return

        val action = PlayerFragmentDirections.actionPlayerFragmentToPlaylistBottomSheet(musicId = musicId)
        this.navigate(action)
    }
}

fun NavController.navigateToPlayer() {
    this.navigate(R.id.player_nav_graph)
}
