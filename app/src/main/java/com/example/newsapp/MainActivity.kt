package com.example.newsapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.R

class MainActivity : AppCompatActivity() {

    private val newsViewModel by lazy {
        (application as MyApplication)
            .appContainer
            .newsViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
    }
}
