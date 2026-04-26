package org.example.project.di

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.example.project.db.NotesDatabase
import org.example.project.device.AndroidDeviceInfo
import org.example.project.device.DeviceInfo
import org.example.project.network.AndroidNetworkMonitor
import org.example.project.network.NetworkMonitor
import org.example.project.notes.NotesRepository
import org.example.project.notes.NotesViewModel
import org.example.project.notes.SettingsRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Create Koin Module dengan context sebagai parameter
 * Menghindari MissingAndroidContextException
 */
fun createAppModule(context: Context) = module {
    // CoroutineScope untuk dependencies - MUST BE FIRST
    single { CoroutineScope(Dispatchers.Main + SupervisorJob()) }
    
    // Device Info
    single<DeviceInfo> { AndroidDeviceInfo(context) }
    
    // Network Monitor
    single<NetworkMonitor> { 
        AndroidNetworkMonitor(
            context = context,
            scope = get()
        )
    }
    
    // Database
    single { 
        NotesDatabase(
            driver = AndroidSqliteDriver(
                schema = NotesDatabase.Schema,
                context = context,
                name = "notes.db"
            )
        )
    }
    
    // Repository
    single { NotesRepository(get()) }
    single { SettingsRepository(context) }
    
    // ViewModel
    viewModel { NotesViewModel(get(), get()) }
}
