package com.example.newsapp.data.repository

import com.example.newsapp.data.local.ApiKeyStorage
import com.example.newsapp.domain.repository.ApiKeyRepository

class ApiKeyRepositoryImpl(
    private val apiKeyStorage: ApiKeyStorage
) : ApiKeyRepository {

    override fun saveApiKey(apiKey: String) {
        apiKeyStorage.saveApiKey(apiKey)
    }

    override fun getApiKey(): String? {
        return apiKeyStorage.getApiKey()
    }

    override fun clearApiKey() {
        apiKeyStorage.clearApiKey()
    }
}