package com.example.newsapp.domain.usecase

import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.NewsRepository

interface NewsUseCase {

    suspend fun getTopHeadlines(
        country: String
    ): List<Article>
}