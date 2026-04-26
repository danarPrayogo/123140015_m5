package org.example.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.device.DeviceInfo
import org.example.project.network.NetworkMonitor
import org.example.project.notes.NoteItem
import org.example.project.notes.NotesUiState
import org.example.project.notes.NotesViewModel
import org.example.project.notes.SortOrder
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

private enum class M7Screen {
    NOTES,
    SETTINGS
}

@Composable
fun App() {
    val viewModel: NotesViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val colorScheme = if (uiState.darkMode) {
        darkColorScheme()
    } else {
        lightColorScheme(
            primary = Color(0xFF1565C0),
            secondary = Color(0xFF00897B),
            surfaceVariant = Color(0xFFEAF2FB)
        )
    }

    MaterialTheme(colorScheme = colorScheme) {
        M7NotesApp(uiState = uiState, viewModel = viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun M7NotesApp(uiState: NotesUiState, viewModel: NotesViewModel) {
    val networkMonitor: NetworkMonitor = koinInject()
    val isNetworkAvailable by networkMonitor.isNetworkAvailable.collectAsState()

    var screen by rememberSaveable { mutableStateOf(M7Screen.NOTES) }
    var editingNote by remember { mutableStateOf<NoteItem?>(null) }
    var showEditor by rememberSaveable { mutableStateOf(false) }
    val snackbars = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage
        if (!message.isNullOrBlank()) {
            snackbars.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Network Status Bar
                if (!isNetworkAvailable) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = Color(0xFFF44336),
                                modifier = Modifier.width(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Tidak Terhubung",
                                color = Color(0xFFF44336),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                CenterAlignedTopAppBar(
                    title = {
                        Text(if (screen == M7Screen.NOTES) "Notes App M7" else "Settings")
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbars) },
        floatingActionButton = {
            if (screen == M7Screen.NOTES) {
                FloatingActionButton(onClick = {
                    editingNote = null
                    showEditor = true
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah catatan")
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = screen == M7Screen.NOTES,
                    onClick = { screen = M7Screen.NOTES },
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Notes") }
                )
                NavigationBarItem(
                    selected = screen == M7Screen.SETTINGS,
                    onClick = { screen = M7Screen.SETTINGS },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") }
                )
            }
        }
    ) { innerPadding ->
        when (screen) {
            M7Screen.NOTES -> NotesContent(
                uiState = uiState,
                onSearchChange = viewModel::onSearchChange,
                onEdit = { note ->
                    editingNote = note
                    showEditor = true
                },
                onToggleFavorite = viewModel::toggleFavorite,
                onDelete = viewModel::deleteNote,
                onRefresh = viewModel::refreshNotes,
                modifier = Modifier.padding(innerPadding)
            )

            M7Screen.SETTINGS -> SettingsContent(
                darkMode = uiState.darkMode,
                sortOrder = uiState.sortOrder,
                onDarkModeChange = viewModel::setDarkMode,
                onSortOrderChange = viewModel::setSortOrder,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    if (showEditor) {
        NoteEditorDialog(
            initialNote = editingNote,
            onDismiss = { showEditor = false },
            onSave = { title, content ->
                if (editingNote == null) {
                    viewModel.addNote(title, content) { showEditor = false }
                } else {
                    viewModel.updateNote(editingNote!!.id, title, content) { showEditor = false }
                }
            }
        )
    }
}

@Composable
private fun NotesContent(
    uiState: NotesUiState,
    onSearchChange: (String) -> Unit,
    onEdit: (NoteItem) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchChange,
            label = { Text("Cari catatan") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )

        Spacer(Modifier.height(12.dp))

        Button(onClick = onRefresh, modifier = Modifier.fillMaxWidth()) {
            Text("Muat Ulang")
        }

        Spacer(Modifier.height(12.dp))

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.notes.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada catatan. Tekan tombol + untuk menambah.")
                }
            }

            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(uiState.notes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onEdit = { onEdit(note) },
                            onToggleFavorite = { onToggleFavorite(note.id) },
                            onDelete = { pendingDeleteId = note.id }
                        )
                    }
                }
            }
        }
    }

    if (pendingDeleteId != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text("Hapus Catatan") },
            text = { Text("Catatan akan dihapus permanen dari database lokal.") },
            confirmButton = {
                Button(onClick = {
                    onDelete(pendingDeleteId!!)
                    pendingDeleteId = null
                }) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                Button(onClick = { pendingDeleteId = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun NoteCard(
    note: NoteItem,
    onEdit: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (note.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (note.isFavorite) Color.Red else MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = note.content,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Text(" Edit")
                }
                Button(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Text(" Hapus")
                }
            }
        }
    }
}

@Composable
private fun SettingsContent(
    darkMode: Boolean,
    sortOrder: SortOrder,
    onDarkModeChange: (Boolean) -> Unit,
    onSortOrderChange: (SortOrder) -> Unit,
    modifier: Modifier = Modifier
) {
    val deviceInfo: DeviceInfo = koinInject()
    val networkMonitor: NetworkMonitor = koinInject()
    val isNetworkAvailable by networkMonitor.isNetworkAvailable.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Network Status
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isNetworkAvailable) 
                        Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isNetworkAvailable) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = if (isNetworkAvailable) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Status Jaringan", style = MaterialTheme.typography.labelSmall)
                            Text(
                                if (isNetworkAvailable) "Terhubung" else "Tidak Terhubung",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            // Device Info
            Text("Informasi Device", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DeviceInfoRow("Manufacturer", deviceInfo.manufacturer)
                    DeviceInfoRow("Model", deviceInfo.model)
                    DeviceInfoRow("Device", deviceInfo.deviceName)
                    DeviceInfoRow("OS", deviceInfo.osVersion)
                    DeviceInfoRow("App Version", deviceInfo.appVersion)
                }
            }
        }

        item {
            // Dark Mode Settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DarkMode, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Mode Gelap")
                }
                Switch(checked = darkMode, onCheckedChange = onDarkModeChange)
            }
        }

        item {
            // Sort Order Settings
            Text("Urutan Catatan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }

        items(SortOrder.entries.size) { index ->
            val option = SortOrder.entries[index]
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = sortOrder == option, onClick = { onSortOrderChange(option) })
                Text(text = option.label())
            }
        }
    }
}

@Composable
private fun DeviceInfoRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun NoteEditorDialog(
    initialNote: NoteItem?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember(initialNote) { mutableStateOf(initialNote?.title.orEmpty()) }
    var content by remember(initialNote) { mutableStateOf(initialNote?.content.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialNote == null) "Tambah Catatan" else "Edit Catatan") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Konten") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, content) },
                enabled = title.isNotBlank() && content.isNotBlank()
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

private fun SortOrder.label(): String = when (this) {
    SortOrder.NEWEST -> "Terbaru"
    SortOrder.OLDEST -> "Terlama"
    SortOrder.TITLE_AZ -> "Judul A-Z"
    SortOrder.TITLE_ZA -> "Judul Z-A"
}
