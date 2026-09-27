package com.example.newsapp.domain.usecase

import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.NewsRepository

class NewsInteractor(
    private val newsRepository: NewsRepository
) : NewsUseCase {

    override suspend fun getTopHeadlines(
        country: String
    ): List<Article> {
        return newsRepository.getTopHeadlines(country)
    }
}