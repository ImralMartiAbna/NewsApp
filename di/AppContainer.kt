package com.example.newsapp.di

import com.example.newsapp.data.remote.NewsApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://newsapi.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: NewsApiService =
        retrofit.create(NewsApiService::class.java)

val remoteDataSource = NewsRemoteDataSource(apiService)

val repository: NewsRepository =
    NewsRepositoryImpl(
        remoteDataSource = remoteDataSource,
        apiKey = "..."
    )

val newsUseCase: NewsUseCase =
    NewsInteractor(repository)

val newsViewModel =
    NewsViewModel(newsUseCase)
}
