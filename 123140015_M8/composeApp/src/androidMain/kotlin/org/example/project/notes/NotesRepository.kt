package org.example.project.notes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.db.NotesDatabase

class NotesRepository(private val database: NotesDatabase) {

    suspend fun getNotes(searchQuery: String, sortOrder: SortOrder): List<NoteItem> = withContext(Dispatchers.IO) {
        val rawNotes = if (searchQuery.isBlank()) {
            database.notesQueries.selectAll().executeAsList()
        } else {
            database.notesQueries.searchNotes(searchQuery).executeAsList()
        }

        rawNotes
            .map {
                NoteItem(
                    id = it.id,
                    title = it.title,
                    content = it.content,
                    isFavorite = it.is_favorite == 1L,
                    createdAt = it.created_at
                )
            }
            .let { notes ->
                when (sortOrder) {
                    SortOrder.NEWEST -> notes.sortedByDescending { it.createdAt }
                    SortOrder.OLDEST -> notes.sortedBy { it.createdAt }
                    SortOrder.TITLE_AZ -> notes.sortedBy { it.title.lowercase() }
                    SortOrder.TITLE_ZA -> notes.sortedByDescending { it.title.lowercase() }
                }
            }
    }

    suspend fun addNote(title: String, content: String) = withContext(Dispatchers.IO) {
        database.notesQueries.insertNote(
            title = title,
            content = content,
            is_favorite = 0L,
            created_at = System.currentTimeMillis()
        )
    }

    suspend fun updateNote(id: Long, title: String, content: String) = withContext(Dispatchers.IO) {
        database.notesQueries.updateNote(title = title, content = content, id = id)
    }

    suspend fun toggleFavorite(id: Long) = withContext(Dispatchers.IO) {
        database.notesQueries.toggleFavorite(id)
    }

    suspend fun deleteNote(id: Long) = withContext(Dispatchers.IO) {
        database.notesQueries.deleteNote(id)
    }
}
