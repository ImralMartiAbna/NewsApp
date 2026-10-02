package com.example.newsapp.data.local

import android.content.Context

class ApiKeyStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "news_app_preferences",
        Context.MODE_PRIVATE
    )

    fun saveApiKey(apiKey: String) {
        preferences.edit()
            .putString("api_key", apiKey)
            .apply()
    }

    fun getApiKey(): String? {
        return preferences.getString("api_key", null)
    }

    fun clearApiKey() {
        preferences.edit()
            .remove("api_key")
            .apply()
    }
}