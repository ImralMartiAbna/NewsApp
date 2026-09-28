package com.example.newsapp.data.mapper

import com.example.newsapp.data.remote.ArticleResponse
import com.example.newsapp.domain.model.Article

object NewsDataMapper {

    fun mapArticleResponseToArticle(
        input: ArticleResponse
    ): Article {
        return Article(
            title = input.title ?: "",
            author = input.author,
            description = input.description,
            url = input.url,
            urlToImage = input.urlToImage,
            publishedAt = input.publishedAt ?: ""
        )
    }
}