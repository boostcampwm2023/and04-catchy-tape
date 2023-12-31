package com.ohdodok.catchytape

import android.content.ComponentName
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import com.ohdodok.catchytape.core.ui.cterror.toMessageId
import com.ohdodok.catchytape.databinding.ActivityMainBinding
import com.ohdodok.catchytape.feature.player.PlayerEvent
import com.ohdodok.catchytape.feature.player.PlayerListener
import com.ohdodok.catchytape.feature.player.PlayerViewModel
import com.ohdodok.catchytape.feature.player.getMediasWithMetaData
import com.ohdodok.catchytape.feature.player.millisecondsPerSecond
import com.ohdodok.catchytape.feature.player.moveNextMedia
import com.ohdodok.catchytape.feature.player.navigateToPlayer
import com.ohdodok.catchytape.feature.player.onPreviousBtnClick
import com.ohdodok.catchytape.mediasession.PlaybackService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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

        setupPlayer()
        setupPlayButton()
        setupPreviousButton()
        setupNextButton()
        observeEvents()
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
                com.ohdodok.catchytape.feature.player.R.id.player_fragment,
                com.ohdodok.catchytape.feature.player.R.id.playlist_bottom_sheet -> {
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
        with(binding.bottomNav) {
            animate()
                .translationY(height.toFloat())
                .setDuration(BOTTOM_NAV_ANIMATION_DURATION)
                .withEndAction { visibility = View.GONE }
                .start()
        }
    }

    private fun showBottomNav() {
        with(binding.bottomNav){
            animate()
                .translationY(0f)
                .setDuration(BOTTOM_NAV_ANIMATION_DURATION)
                .withStartAction { visibility = View.VISIBLE }
                .start()
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

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                playViewModel.events.collectLatest { event ->
                    when (event) {
                        is PlayerEvent.ShowError -> {
                            Toast.makeText(this@MainActivity, getString(event.error.toMessageId()), Toast.LENGTH_LONG).show()
                        }

                        is PlayerEvent.PlaylistChanged -> {
                            val newItems = getMediasWithMetaData(event.currentPlaylist.musics)
                            player.clearMediaItems()
                            player.setMediaItems(newItems)

                            player.seekTo(event.currentPlaylist.startMusicIndex, 0)
                            player.play()
                        }
                    }
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
            player.onPreviousBtnClick()
        }
    }

    private fun setupNextButton() {
        binding.pcvController.setOnNextButtonClick {
            player.moveNextMedia()
        }
    }
}