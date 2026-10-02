package com.example.newsapp.domain.repository

interface ApiKeyRepository {

    fun saveApiKey(apiKey: String)

    fun getApiKey(): String?

    fun clearApiKey()
}