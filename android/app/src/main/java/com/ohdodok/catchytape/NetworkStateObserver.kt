package com.ohdodok.catchytape

import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class NetworkStateObserver(
    private val connectivityManager: ConnectivityManager,
    checkNetworkState: () -> Unit
) : DefaultLifecycleObserver {

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            super.onLost(network)
            checkNetworkState()
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}