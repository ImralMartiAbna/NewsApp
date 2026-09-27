package com.example.newsapp.domain.repository

import com.example.newsapp.domain.model.Article

interface NewsRepository {

    suspend fun getTopHeadlines(
        country: String
    ): List<Article>
}