package org.example.project.ui.news

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.example.project.data.model.Article

@Composable
fun NewsDetailScreen(article: Article) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = article.image_url,
            contentDescription = article.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentScale = ContentScale.Crop
        )

        Text(
            text = article.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        if (article.published_at.isNotBlank()) {
            Text(
                text = "Dipublikasikan: ${article.published_at}",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            text = if (article.summary.isBlank()) "Deskripsi tidak tersedia." else article.summary,
            style = MaterialTheme.typography.bodyLarge
        )

        if (!article.url.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { uriHandler.openUri(article.url) }) {
                Text("Baca Artikel Asli")
            }
        }
    }
}