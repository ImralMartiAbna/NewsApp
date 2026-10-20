package com.example.newsapp.di

import android.content.Context
import android.util.Log
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
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(
    private val context: Context
) {
    private val okHttpClient: OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .header("Accept", "application/json")
                    .header("User-Agent", "NewsApp/1.0 (Android)")
                    .build()

                val response = chain.proceed(request)

                Log.d(
                    "NewsApiDebug",
                    "HTTP=${response.code()} " +
                        "path=${response.request().url().encodedPath()} " +
                        "contentType=${response.header("Content-Type")} " +
                        "server=${response.header("Server")} " +
                        "cfRay=${response.header("CF-Ray")}"
                )

                if (!response.isSuccessful) {
                    val body = response.peekBody(8192).string()
                        .replace("\n", " ")
                        .replace("\r", " ")
                        .take(1000)

                    Log.e(
                        "NewsApiDebug",
                        "Error response body=$body"
                    )
                }

                response
            }
            .build()

    private val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()

    val apiService: NewsApiService =
        retrofit.create(NewsApiService::class.java)

    val remoteDataSource =
        NewsRemoteDataSource(apiService)

    val apiKeyStorage =
        ApiKeyStorage(context)

    val apiKeyRepository: ApiKeyRepository =
        ApiKeyRepositoryImpl(
            apiKeyStorage = apiKeyStorage
        )

    val repository: NewsRepository =
        NewsRepositoryImpl(
            remoteDataSource = remoteDataSource,
            apiKeyRepository = apiKeyRepository
        )

    val newsUseCase: NewsUseCase =
        NewsInteractor(repository)

    val requestOtpUseCase =
        RequestOtpUseCase()

    val verifyOtpUseCase =
        VerifyOtpUseCase()

    val newsViewModel =
        NewsViewModel(
            newsUseCase = newsUseCase
        )
}
