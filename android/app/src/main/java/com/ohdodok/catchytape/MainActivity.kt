package com.ohdodok.catchytape

import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import com.ohdodok.catchytape.core.ui.R.string as uiString

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var connectivityManager: ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}