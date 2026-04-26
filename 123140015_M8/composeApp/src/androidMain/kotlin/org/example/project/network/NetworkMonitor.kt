package org.example.project.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Android implementation untuk NetworkMonitor menggunakan ConnectivityManager
 */
class AndroidNetworkMonitor(
    context: Context,
    private val scope: CoroutineScope
) : NetworkMonitor {
    private val connectivityManager = context.getSystemService<ConnectivityManager>()

    private val networkFlow: Flow<Boolean> = callbackFlow {
        // Emit initial state
        val initialState = connectivityManager?.activeNetwork != null
        trySend(initialState)

        if (connectivityManager != null) {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    trySend(true)
                }

                override fun onLost(network: Network) {
                    val isConnected = connectivityManager.activeNetwork != null
                    trySend(isConnected)
                }

                override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                    trySend(true)
                }
            }

            try {
                connectivityManager.registerDefaultNetworkCallback(callback)
                awaitClose {
                    try {
                        connectivityManager.unregisterNetworkCallback(callback)
                    } catch (e: Exception) {
                        // Ignore unregister errors
                    }
                }
            } catch (e: Exception) {
                // If registration fails, just close the flow
                close(e)
            }
        } else {
            // If ConnectivityManager is not available, close the flow
            close()
        }
    }

    override val isNetworkAvailable: StateFlow<Boolean> = networkFlow.stateIn(
        scope = scope,
        started = kotlinx.coroutines.flow.SharingStarted.Lazily,
        initialValue = connectivityManager?.activeNetwork != null
    )
}

actual fun createNetworkMonitor(): NetworkMonitor {
    // Akan di-inject via Koin, tapi untuk fallback
    // Context akan diprovide via Koin
    return object : NetworkMonitor {
        override val isNetworkAvailable = kotlinx.coroutines.flow.MutableStateFlow(true)
    }
}
