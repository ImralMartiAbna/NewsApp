package com.example.newsapp.data.remote

import android.util.Log

class NewsRemoteDataSource(
    private val apiService: NewsApiService
) {
    suspend fun getTopHeadlines(
        country: String,
        apiKey: String,
        category: String? = null
    ): NewsResponse {

        Log.d(
            "NewsApiDebug",
            "country=$country, category=$category, " +
                "apiKeyPresent=${apiKey.isNotBlank()}, " +
                "apiKeyLength=${apiKey.length}"
        )

        return apiService.getTopHeadlines(
            country = country,
            apiKey = apiKey,
            category = category
        )
    }
}
