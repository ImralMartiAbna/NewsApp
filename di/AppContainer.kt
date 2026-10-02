package com.example.newsapp.di

import android.content.Context
import com.example.newsapp.data.local.ApiKeyStorage
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.data.remote.NewsRemoteDataSource
import com.example.newsapp.data.repository.ApiKeyRepositoryImpl
import com.example.newsapp.data.repository.NewsRepositoryImpl
import com.example.newsapp.domain.repository.ApiKeyRepository
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.domain.usecase.NewsInteractor
import com.example.newsapp.domain.usecase.NewsUseCase
import com.example.newsapp.domain.usecase.RequestOtpUseCase
import com.example.newsapp.domain.usecase.VerifyOtpUseCase
import com.example.newsapp.presentation.viewmodel.NewsViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(
    private val context: Context
) {

    private val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()

    val apiService: NewsApiService =
        retrofit.create(NewsApiService::class.java)

    val remoteDataSource =
        NewsRemoteDataSource(apiService)

    val repository: NewsRepository =
        NewsRepositoryImpl(
            remoteDataSource = remoteDataSource
        )

    val newsUseCase: NewsUseCase =
        NewsInteractor(repository)

    val apiKeyStorage =
        ApiKeyStorage(context)

    val apiKeyRepository: ApiKeyRepository =
        ApiKeyRepositoryImpl(
            apiKeyStorage = apiKeyStorage
        )

    val requestOtpUseCase =
        RequestOtpUseCase()

    val verifyOtpUseCase =
        VerifyOtpUseCase()

    val newsViewModel =
        NewsViewModel(
            newsUseCase = newsUseCase,
            apiKeyRepository = apiKeyRepository
        )
}