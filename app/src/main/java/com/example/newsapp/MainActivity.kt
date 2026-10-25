
package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.newsapp.presentation.MainActivity2
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val appContainer by lazy {
        (application as MyApplication).appContainer
    }

    private val newsViewModel by lazy {
        appContainer.newsViewModel
    }

    private var navigationStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etApiKey = findViewById<EditText>(R.id.etApiKey)
        val btnSaveApiKey = findViewById<Button>(R.id.btnSaveApiKey)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    newsViewModel.articles.collect { articles ->
                        if (articles.isNotEmpty() && !navigationStarted) {
                            navigationStarted = true

                            tvStatus.text =
                                "Berhasil mendapatkan ${articles.size} berita."

                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    MainActivity2::class.java
                                )
                            )
                        }
                    }
                }

                launch {
                    newsViewModel.errorMessage.collect { message ->
                        if (!message.isNullOrBlank()) {
                            tvStatus.text = message
                        }
                    }
                }
            }
        }

        btnSaveApiKey.setOnClickListener {
            val apiKey = etApiKey.text.toString().trim()

            if (apiKey.isBlank()) {
                tvStatus.text = "Masukkan API Key terlebih dahulu."
                return@setOnClickListener
            }

            val savedApiKey =
                appContainer.apiKeyRepository.getApiKey()

            if (savedApiKey.isNullOrBlank()) {
                appContainer.apiKeyRepository.saveApiKey(apiKey)
                etApiKey.text.clear()

                tvStatus.text =
                    "API Key tersimpan. Buka kembali aplikasi dan masukkan API Key untuk verifikasi."

                return@setOnClickListener
            }

            if (apiKey != savedApiKey) {
                tvStatus.text =
                    "API Key tidak cocok dengan yang tersimpan."

                return@setOnClickListener
            }

            navigationStarted = false
            etApiKey.text.clear()

            tvStatus.text = "API Key cocok. Mengambil berita..."
            newsViewModel.getTopHeadlines("us")
        }
    }
}
