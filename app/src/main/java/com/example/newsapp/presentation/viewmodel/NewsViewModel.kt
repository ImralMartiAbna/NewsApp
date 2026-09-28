package com.example.newsapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.usecase.NewsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel(
    private val newsUseCase: NewsUseCase
) : ViewModel() {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles

    fun getTopHeadlines(country: String) {
        viewModelScope.launch {
            _articles.value = newsUseCase.getTopHeadlines(country)
        }
    }
}