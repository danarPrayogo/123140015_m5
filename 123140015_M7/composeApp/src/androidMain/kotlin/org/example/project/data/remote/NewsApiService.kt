package org.example.project.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.example.project.data.model.NewsResponse

class NewsApiService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
        }
    }

    suspend fun fetchNews(): NewsResponse {
        // Menggunakan Spaceflight News API karena tidak memerlukan API Key dan menyediakan gambar
        return client.get("https://api.spaceflightnewsapi.net/v4/articles/").body()
    }
}
