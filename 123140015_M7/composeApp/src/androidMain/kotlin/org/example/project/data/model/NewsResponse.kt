package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse(
    val results: List<Article> = emptyList()
)

@Serializable
data class Article(
    val id: Int,
    val title: String = "Tanpa Judul",
    val summary: String = "",
    val image_url: String? = null,
    val published_at: String = "",
    val url: String? = null
)
