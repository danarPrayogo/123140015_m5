package org.example.project.device

import android.os.Build
import android.content.Context
import android.content.pm.PackageManager

/**
 * Android implementation untuk DeviceInfo
 */
class AndroidDeviceInfo(context: Context) : DeviceInfo {
    override val deviceName: String = Build.DEVICE ?: "Unknown"
    override val osVersion: String = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    override val manufacturer: String = Build.MANUFACTURER ?: "Unknown"
    override val model: String = Build.MODEL ?: "Unknown"
    override val appVersion: String = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
    } catch (e: PackageManager.NameNotFoundException) {
        "1.0"
    }
}

actual fun getDeviceInfo(): DeviceInfo {
    // Akan di-inject via Koin, tapi untuk fallback buat instance baru
    // Context akan diprovide via Koin
    return object : DeviceInfo {
        override val deviceName: String = Build.DEVICE ?: "Unknown"
        override val osVersion: String = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
        override val manufacturer: String = Build.MANUFACTURER ?: "Unknown"
        override val model: String = Build.MODEL ?: "Unknown"
        override val appVersion: String = "1.0"
    }
}
