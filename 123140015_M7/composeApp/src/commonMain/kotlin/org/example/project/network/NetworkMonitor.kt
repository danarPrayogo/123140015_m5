package org.example.project.network

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface untuk memonitor status jaringan
 */
interface NetworkMonitor {
    val isNetworkAvailable: StateFlow<Boolean>
}

// Expect declaration untuk platform-specific implementation
expect fun createNetworkMonitor(): NetworkMonitor
