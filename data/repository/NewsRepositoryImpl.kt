package com.example.newsapp.data.repository

import com.example.newsapp.data.mapper.NewsDataMapper
import com.example.newsapp.data.remote.NewsRemoteDataSource
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.NewsRepository

class NewsRepositoryImpl(
    private val remoteDataSource: NewsRemoteDataSource,
    private val apiKey: String
) : NewsRepository {

    override suspend fun getTopHeadlines(
        country: String
    ): List<Article> {

        val response = remoteDataSource.getTopHeadlines(
            country = country,
            apiKey = apiKey
        )

        return response.articles.map {
            NewsDataMapper.mapArticleResponseToArticle(it)
        }
    }
}