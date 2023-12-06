package com.ohdodok.catchytape

import android.animation.ObjectAnimator
import android.content.ComponentName
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import com.ohdodok.catchytape.feature.player.getMediasWithMetaData
import com.ohdodok.catchytape.feature.player.millisecondsPerSecond
import com.ohdodok.catchytape.feature.player.moveNextMedia
import com.ohdodok.catchytape.feature.player.movePreviousMedia
import com.ohdodok.catchytape.feature.player.navigateToPlayer
import com.ohdodok.catchytape.mediasession.PlaybackService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.ohdodok.catchytape.core.ui.R.string as uiString

private const val BOTTOM_NAV_ANIMATION_DURATION = 700L

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

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setupBottomNav()
        setUpPlayerController()
        connectivityManager = getSystemService(ConnectivityManager::class.java)
        checkNetworkState()

        val networkStateObserver = NetworkStateObserver(connectivityManager, ::checkNetworkState)
        lifecycle.addObserver(networkStateObserver)

        playViewModel.getPlayedMusicInfo()
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

    override fun onStop() {
        super.onStop()
        val a = player.currentMediaItem
        val b = player.currentPosition.toInt()
        val c = player.currentTracks
        Timber.d("${a},${b},${c}")
        playViewModel.savePlayedMusicInfo()
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
                    showPlayerController()
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
        val height = binding.bottomNav.height.toFloat()
        ObjectAnimator.ofFloat(binding.bottomNav, "translationY", height).apply {
            duration = BOTTOM_NAV_ANIMATION_DURATION
            doOnEnd { binding.bottomNav.visibility = View.GONE }
            start()
        }
    }

    private fun showBottomNav() {
        binding.bottomNav.visibility = View.VISIBLE
        ObjectAnimator.ofFloat(binding.bottomNav, "translationY", 0f).apply {
            duration = BOTTOM_NAV_ANIMATION_DURATION
            start()
        }
    }

    private fun hidePlayerController() {
        binding.pcvController.visibility = View.GONE
    }

    private fun showPlayerController() {
        binding.pcvController.visibility = View.VISIBLE
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
                playViewModel.currentPlaylist.filterNotNull().collect {
                    val newItems = getMediasWithMetaData(it.musics)
                    player.clearMediaItems()
                    player.setMediaItems(newItems)

                    player.seekTo(it.startMusicIndex, 0)
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
            player.movePreviousMedia()
        }
    }

    private fun setupNextButton() {
        binding.pcvController.setOnNextButtonClick {
            player.moveNextMedia()
        }
    }
}