package org.example.project.data.repositories

import org.example.project.data.model.Article
import org.example.project.data.remote.NewsApiService
import javax.inject.Inject
import javax.inject.Named

class NewsRepository @Inject constructor(
    @Named("external") private val newsApiService: NewsApiService
) {
    suspend fun getArticles(keyword: String): List<Article> {
        return newsApiService.getArticles(keyword).articles
    }
}