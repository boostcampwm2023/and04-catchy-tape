package com.ohdodok.catchytape

import android.content.ComponentName
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ohdodok.catchytape.databinding.ActivityMainBinding
import com.ohdodok.catchytape.feature.player.PlayerListener
import com.ohdodok.catchytape.feature.player.PlayerViewModel
import com.ohdodok.catchytape.feature.player.millisecondsPerSecond
import com.ohdodok.catchytape.feature.player.navigateToPlayer
import com.ohdodok.catchytape.mediacontrol.PlaybackService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ohdodok.catchytape.core.ui.R.string as uiString

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var player: ExoPlayer
    private lateinit var binding: ActivityMainBinding
    private lateinit var connectivityManager: ConnectivityManager
    private val playViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewModel = playViewModel
        binding.lifecycleOwner = this

        setupBottomNav()
        setUpPlayerController()
        connectivityManager = getSystemService(ConnectivityManager::class.java)
        checkNetworkState()

        val networkStateObserver = NetworkStateObserver(connectivityManager, ::checkNetworkState)
        lifecycle.addObserver(networkStateObserver)

        setupPlayer()
        setupPlayButton()
        setupPreviousButton()
        setupNextButton()
        observePlaylistChange()
    }

    override fun onStart() {
        super.onStart()
        connectToMediaSession()
    }

    private fun connectToMediaSession() {
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        MediaController.Builder(this, sessionToken).buildAsync()
    }

    private fun checkNetworkState() {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        val isNetworkAvailable = capabilities?.hasCapability(NET_CAPABILITY_VALIDATED) ?: false

        if (!isNetworkAvailable) {
            Toast.makeText(this, getString(uiString.check_network), Toast.LENGTH_LONG).show()
        }
    }

    private fun setupBottomNav() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.bottomNav.setupWithNavController(navHostFragment.navController)

        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                com.ohdodok.catchytape.feature.player.R.id.player_fragment -> {
                    hideBottomNav()
                    hidePlayerController()
                }

                else -> {
                    showBottomNav()
                }
            }
        }
    }

    private fun setUpPlayerController() {
        binding.pcvController.setOnClickListener {
            binding.navHostFragment.findNavController().navigateToPlayer()
        }
    }

    private fun hideBottomNav() {
        binding.bottomNav.visibility = View.GONE
    }

    private fun showBottomNav() {
        binding.bottomNav.visibility = View.VISIBLE
    }

    private fun hidePlayerController() {
        binding.pcvController.visibility = View.GONE
    }

    private fun setupPlayer() {
        player.addListener(PlayerListener(playViewModel))
        player.prepare()
        setupPlayerTimer()
    }

    private fun setupPlayerTimer() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    delay(1000L)
                    if (player.isPlaying) {
                        val positionMs = player.currentPosition.toInt()
                        playViewModel.updateCurrentPosition(positionMs / millisecondsPerSecond)
                    }
                }
            }
        }
    }

    private fun observePlaylistChange() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                playViewModel.playlistChangeEvent.consumeEach { newPlaylist ->
                    val newItems = newPlaylist.musics.map {
                        // todo : it에서 url 찾아서 넣어주기!
                        MediaItem.Builder().setUri("https://catchy-tape-bucket2.kr.object.ncloudstorage.com/music/8ce4b6b0-6480-479a-99b9-54ef7e913313/music.m3u8")
                            .setMediaId(it.id)
                            .build()
                    }
                    player.clearMediaItems()
                    player.setMediaItems(newItems)

                    val index = newPlaylist.musics.indexOf(newPlaylist.startMusic)
                    player.seekTo(index, 0)
                    player.play()
                }
            }
        }
    }

    private fun setupPlayButton() {
        binding.pcvController.setOnPlayButtonClick {
            if (playViewModel.uiState.value.isPlaying) player.pause()
            else player.play()
        }
    }

    private fun setupPreviousButton() {
        binding.pcvController.setOnPreviousButtonClick {
            player.seekToPreviousMediaItem()
            player.play()
        }
    }

    private fun setupNextButton() {
        binding.pcvController.setOnNextButtonClick {
            player.seekToNextMediaItem()
            player.play()
        }
    }
}