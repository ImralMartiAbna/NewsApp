package com.example.newsapp.domain.model

data class Article(
    val title: String,
    val author: String?,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String
)