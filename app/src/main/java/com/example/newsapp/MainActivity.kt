package com.example.newsapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val appContainer by lazy {
        (application as MyApplication).appContainer
    }

    private val newsViewModel by lazy {
        appContainer.newsViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etApiKey = findViewById<EditText>(R.id.etApiKey)
        val btnSaveApiKey = findViewById<Button>(R.id.btnSaveApiKey)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)

        lifecycleScope.launch {
            newsViewModel.articles.collect { articles ->
                if (articles.isNotEmpty()) {
                    tvStatus.text =
                        "Berhasil mendapatkan ${articles.size} berita."
                }
            }
        }

        lifecycleScope.launch {
            newsViewModel.errorMessage.collect { message ->
                if (message != null) {
                    tvStatus.text = message
                }
            }
        }

        btnSaveApiKey.setOnClickListener {
            val apiKey = etApiKey.text.toString().trim()

            if (apiKey.isBlank()) {
                tvStatus.text = "Masukkan API Key terlebih dahulu."
                return@setOnClickListener
            }

            val savedApiKey = appContainer.apiKeyRepository.getApiKey()

            if (savedApiKey.isNullOrBlank()) {
                appContainer.apiKeyRepository.saveApiKey(apiKey)
                etApiKey.text.clear()
                tvStatus.text = "API Key tersimpan. Buka kembali aplikasi dan masukkan API Key untuk verifikasi."
                return@setOnClickListener
            }

            if (apiKey != savedApiKey) {
                tvStatus.text = "API Key tidak cocok dengan yang tersimpan."
                return@setOnClickListener
            }

            etApiKey.text.clear()

            tvStatus.text = "API Key tersimpan. Mengambil berita..."
            newsViewModel.getTopHeadlines("us")
        }


    }
}
