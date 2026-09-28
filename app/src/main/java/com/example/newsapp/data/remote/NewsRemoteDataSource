package com.example.newsapp.data.remote

class NewsRemoteDataSource(
    private val apiService: NewsApiService
) {

    suspend fun getTopHeadlines(
        country: String,
        apiKey: String
    ): NewsResponse {
        return apiService.getTopHeadlines(
            country = country,
            apiKey = apiKey
        )
    }
}