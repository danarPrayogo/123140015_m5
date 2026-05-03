package org.example.project

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.project.data.model.Article
import org.example.project.ui.news.NewsDetailScreen
import org.example.project.ui.news.NewsScreen
import org.example.project.ui.news.NewsViewModel

private object NewsRoute {
    const val LIST = "news-list"
    const val DETAIL = "news-detail"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsApp(
    viewModel: NewsViewModel = remember { NewsViewModel() }
) {
    val navController = rememberNavController()
    var selectedArticle by remember { mutableStateOf<Article?>(null) }

    MaterialTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (selectedArticle == null) "News Reader Danar" else selectedArticle?.title.orEmpty(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        if (selectedArticle != null) {
                            IconButton(onClick = {
                                selectedArticle = null
                                navController.popBackStack()
                            }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = NewsRoute.LIST,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(NewsRoute.LIST) {
                    NewsScreen(
                        viewModel = viewModel,
                        onArticleClick = { article ->
                            selectedArticle = article
                            navController.navigate(NewsRoute.DETAIL)
                        }
                    )
                }

                composable(NewsRoute.DETAIL) {
                    val article = selectedArticle
                    if (article != null) {
                        NewsDetailScreen(article = article)
                    } else {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}