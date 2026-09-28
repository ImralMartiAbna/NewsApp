package com.example.newsapp.di

import android.app.Application

class MyApplication : Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer()
    }
}