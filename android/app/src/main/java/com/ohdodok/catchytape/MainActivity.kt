package com.ohdodok.catchytape

import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ohdodok.catchytape.databinding.ActivityMainBinding
import com.ohdodok.catchytape.feature.player.navigateToPlayer
import dagger.hilt.android.AndroidEntryPoint
import com.ohdodok.catchytape.core.ui.R.string as uiString

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var connectivityManager: ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNav()

        connectivityManager = getSystemService(ConnectivityManager::class.java)
        checkNetworkState()

        val networkStateObserver = NetworkStateObserver(connectivityManager, ::checkNetworkState)
        lifecycle.addObserver(networkStateObserver)
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
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.bottomNav.setupWithNavController(navHostFragment.navController)

        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                com.ohdodok.catchytape.feature.player.R.id.player_fragment -> {
                    binding.bottomNav.visibility = View.GONE
                    binding.pcvController.visibility = View.GONE
                }

                else -> {
                    binding.bottomNav.visibility = View.VISIBLE
                    binding.pcvController.visibility = View.VISIBLE
                }
            }
        }
        setUpPC()
    }

    private fun setUpPC() {
        binding.pcvController.setOnClickListener {
            binding.navHostFragment.findNavController().navigateToPlayer()
        }
    }

}

