package org.example.project.notes

data class NoteItem(
    val id: Long,
    val title: String,
    val content: String,
    val isFavorite: Boolean,
    val createdAt: Long
)

enum class SortOrder {
    NEWEST,
    OLDEST,
    TITLE_AZ,
    TITLE_ZA
}

data class UserSettings(
    val darkMode: Boolean = false,
    val sortOrder: SortOrder = SortOrder.NEWEST
)
