package org.example.project.ai

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.isSuccess
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.example.project.BuildConfig

private const val GEMINI_MODEL = "gemini-2.5-flash"

class GeminiService {
	private val client = HttpClient(OkHttp) {
		install(ContentNegotiation) {
			json(
				Json {
					ignoreUnknownKeys = true
					explicitNulls = false
				}
			)
		}
	}

	suspend fun summarizeNote(title: String, content: String): String = withContext(Dispatchers.IO) {
		val apiKey = BuildConfig.GEMINI_API_KEY.trim()
		if (apiKey.isBlank()) {
			throw IllegalStateException(
				"API key Gemini belum diatur. Tambahkan GEMINI_API_KEY di local.properties."
			)
		}

		val systemPrompt = """
			Kamu adalah asisten peringkas catatan untuk aplikasi notes.
			Tulis hasil dalam Bahasa Indonesia yang singkat, jelas, dan natural.
			Ringkas isi catatan menjadi 3-5 poin penting.
			Jika ada langkah tindak lanjut, tambahkan bagian "Tindak lanjut".
			Jangan menambahkan informasi yang tidak ada di catatan.
		""".trimIndent()

		val userMessage = buildString {
			append(systemPrompt)
			appendLine()
			appendLine()
			appendLine("Judul catatan: $title")
			appendLine("Isi catatan:")
			append(content)
		}

		val requestBody = GeminiGenerateContentRequest(
			contents = listOf(
				GeminiContent(
					role = "user",
					parts = listOf(
						GeminiPart(
							text = userMessage
						)
					)
				)
			)
		)

		val response = client.post("https://generativelanguage.googleapis.com/v1/models/$GEMINI_MODEL:generateContent") {
			url {
				parameters.append("key", apiKey)
			}
			contentType(ContentType.Application.Json)
			setBody(requestBody)
		}

		val responseBody = runCatching {
			response.body<GeminiGenerateContentResponse>()
		}.getOrElse { error ->
			throw IllegalStateException("Gagal membaca respons Gemini: ${error.message}")
		}

		if (!response.status.isSuccess()) {
			throw IllegalStateException(
				responseBody.error?.message ?: "Gemini API gagal dengan status ${response.status.value}."
			)
		}

		val summary = responseBody.candidates
			.firstOrNull()
			?.content
			?.parts
			.orEmpty()
			.joinToString(separator = "") { it.text.orEmpty() }
			.trim()

		if (summary.isBlank()) {
			throw IllegalStateException("Gemini tidak mengembalikan ringkasan.")
		}

		summary
	}
}

@Serializable
data class GeminiGenerateContentRequest(
	val contents: List<GeminiContent>,
	@SerialName("generationConfig")
	val generationConfig: GeminiGenerationConfig = GeminiGenerationConfig()
)

@Serializable
data class GeminiGenerationConfig(
	val temperature: Double = 0.2,
	@SerialName("topP")
	val topP: Double = 0.9,
	@SerialName("maxOutputTokens")
	val maxOutputTokens: Int = 256
)

@Serializable
data class GeminiContent(
	val role: String? = null,
	val parts: List<GeminiPart> = emptyList()
)

@Serializable
data class GeminiPart(
	val text: String? = null
)

@Serializable
data class GeminiGenerateContentResponse(
	val candidates: List<GeminiCandidate> = emptyList(),
	val error: GeminiError? = null
)

@Serializable
data class GeminiCandidate(
	val content: GeminiContent? = null
)

@Serializable
data class GeminiError(
	val code: Int? = null,
	val message: String? = null,
	val status: String? = null
)
