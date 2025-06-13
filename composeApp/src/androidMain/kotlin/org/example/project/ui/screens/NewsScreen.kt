package org.example.project.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import org.example.project.data.model.Article
import org.example.project.ui.viewmodel.NewsViewModel

@Composable
fun NewsScreen(viewModel: NewsViewModel = hiltViewModel()) {
    val articles by viewModel.articles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var query by remember { mutableStateOf("college violence") }

    LaunchedEffect(Unit) {
        viewModel.fetchArticles(query)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(query) {
            query = it
            viewModel.fetchArticles(query)
        }

        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
            }

            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(articles) { article ->
                    ArticleItem(article)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit
) {
    Surface(
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
            singleLine = true,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            if (value.isEmpty()) {
                Text("Search news...", color = Color.Gray)
            }
            it()
        }
    }
}

@Composable
fun ArticleItem(article: Article) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                article.url.takeIf { it.isNotBlank() }?.let { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            }
    ) {
        // Title
        Text(
            text = article.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Image
        article.urlToImage?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Article Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(vertical = 8.dp)
            )
        }

        // Description
        Text(
            text = article.description ?: "No description available",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Author, source, and published date
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "By ${article.author ?: "Unknown"}",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = article.source.name,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }

        Text(
            text = article.publishedAt.take(10), // Format: YYYY-MM-DD
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
