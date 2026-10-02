package com.example.newsapp

import android.app.Application
import com.example.newsapp.di.AppContainer

class MyApplication : Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer(this)
    }
}