package org.example.project.data.repository

import org.example.project.data.model.Article
import org.example.project.data.remote.NewsApiService

class NewsRepository(private val apiService: NewsApiService) {
    suspend fun getNews(): List<Article> {
        return apiService.fetchNews().results
    }
}
