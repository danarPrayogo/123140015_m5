package org.example.project.device

/**
 * Interface untuk mendapatkan informasi device
 */
interface DeviceInfo {
    val deviceName: String
    val osVersion: String
    val manufacturer: String
    val model: String
    val appVersion: String
}

// Expect declaration untuk platform-specific implementation
expect fun getDeviceInfo(): DeviceInfo
