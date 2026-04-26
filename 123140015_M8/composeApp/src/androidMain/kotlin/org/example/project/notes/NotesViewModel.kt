package org.example.project.notes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.db.NotesDatabase

data class NotesUiState(
    val isLoading: Boolean = true,
    val notes: List<NoteItem> = emptyList(),
    val searchQuery: String = "",
    val darkMode: Boolean = false,
    val sortOrder: SortOrder = SortOrder.NEWEST,
    val errorMessage: String? = null
)

class NotesViewModel(
    private val notesRepository: NotesRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        observeSettings()
        refreshNotes()
    }

    fun onSearchChange(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
        refreshNotes()
    }

    fun addNote(title: String, content: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching {
                notesRepository.addNote(title.trim(), content.trim())
            }.onSuccess {
                refreshNotes()
                onDone()
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message ?: "Gagal menambah catatan") }
            }
        }
    }

    fun updateNote(id: Long, title: String, content: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching {
                notesRepository.updateNote(id, title.trim(), content.trim())
            }.onSuccess {
                refreshNotes()
                onDone()
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message ?: "Gagal memperbarui catatan") }
            }
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            runCatching {
                notesRepository.toggleFavorite(id)
            }.onSuccess {
                refreshNotes()
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message ?: "Gagal mengubah favorit") }
            }
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            runCatching {
                notesRepository.deleteNote(id)
            }.onSuccess {
                refreshNotes()
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message ?: "Gagal menghapus catatan") }
            }
        }
    }

    fun setDarkMode(value: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkMode(value)
        }
    }

    fun setSortOrder(value: SortOrder) {
        viewModelScope.launch {
            settingsRepository.setSortOrder(value)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun refreshNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                notesRepository.getNotes(
                    searchQuery = _uiState.value.searchQuery,
                    sortOrder = _uiState.value.sortOrder
                )
            }.onSuccess { notes ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        notes = notes,
                        errorMessage = null
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.message ?: "Terjadi kesalahan"
                    )
                }
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                _uiState.update {
                    it.copy(
                        darkMode = settings.darkMode,
                        sortOrder = settings.sortOrder
                    )
                }
                refreshNotes()
            }
        }
    }
}

class NotesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val driver = AndroidSqliteDriver(NotesDatabase.Schema, context, "m7_notes.db")
        val database = NotesDatabase(driver)
        val notesRepository = NotesRepository(database)
        val settingsRepository = SettingsRepository(context)

        return NotesViewModel(notesRepository, settingsRepository) as T
    }
}
