package org.example.project.data.remote

import org.example.project.BuildConfig
import org.example.project.data.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("v2/everything")
    suspend fun getArticles(
        @Query("q") keyword: String = "college violence",
        @Query("apiKey") apiKey: String = BuildConfig.NEWS_API_KEY,
        @Query("language") language: String = "en",
    ): NewsResponse
}