package org.example.project.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.model.Article
import org.example.project.data.remote.NewsApiService
import org.example.project.data.repository.NewsRepository

sealed interface NewsUiState {
	data object Loading : NewsUiState
	data class Success(val articles: List<Article>) : NewsUiState
	data class Error(val message: String) : NewsUiState
}

class NewsViewModel(
	private val repository: NewsRepository = NewsRepository(NewsApiService())
) : ViewModel() {

	private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
	val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

	private val _isRefreshing = MutableStateFlow(false)
	val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

	init {
		loadNews()
	}

	fun loadNews() {
		viewModelScope.launch {
			_uiState.value = NewsUiState.Loading
			try {
				val articles = repository.getNews()
				_uiState.value = NewsUiState.Success(articles)
			} catch (e: Exception) {
				_uiState.value = NewsUiState.Error(e.message ?: "Gagal memuat berita")
			}
		}
	}

	fun refreshNews() {
		viewModelScope.launch {
			_isRefreshing.value = true
			try {
				val articles = repository.getNews()
				_uiState.value = NewsUiState.Success(articles)
			} catch (e: Exception) {
				_uiState.value = NewsUiState.Error(e.message ?: "Gagal memuat berita")
			} finally {
				_isRefreshing.value = false
			}
		}
	}
}
