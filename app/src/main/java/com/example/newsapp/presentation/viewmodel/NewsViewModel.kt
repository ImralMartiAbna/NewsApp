package com.example.newsapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.usecase.NewsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class NewsViewModel(
    private val newsUseCase: NewsUseCase
) : ViewModel() {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun getTopHeadlines(country: String) {
        viewModelScope.launch {
            _errorMessage.value = null

            try {
                _articles.value = newsUseCase.getTopHeadlines(country)
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                val response = e.response()
                val body = runCatching {
                    response?.errorBody()?.string().orEmpty()
                }.getOrDefault("")

                val headers = response?.headers()
                val contentType = headers?.get("Content-Type").orEmpty()
                val server = headers?.get("Server").orEmpty()
                val via = headers?.get("Via").orEmpty()

                val json = runCatching {
                    JSONObject(body)
                }.getOrNull()

                val code = json?.optString("code").orEmpty()
                val message = json?.optString("message").orEmpty()

                _errorMessage.value = buildString {
                    append("HTTP ${e.code()}")
                    if (code.isNotBlank()) {
                        append("\nKode: ")
                        append(code)
                    }
                    if (message.isNotBlank()) {
                        append("\nPesan: ")
                        append(message)
                    }
                    if (contentType.isNotBlank()) {
                        append("\nContent-Type: ")
                        append(contentType)
                    }
                    if (server.isNotBlank()) {
                        append("\nServer: ")
                        append(server)
                    }
                    if (via.isNotBlank()) {
                        append("\nVia: ")
                        append(via)
                    }
                    if (body.isNotBlank()) {
                        append("\nRespons: ")
                        append(body.take(300))
                    } else {
                        append("\nIsi respons kosong.")
                    }
                }
            } catch (e: IOException) {
                _errorMessage.value =
                    "Koneksi internet bermasalah. Periksa jaringan lalu coba lagi."
            } catch (e: Exception) {
                _errorMessage.value =
                    "Terjadi kesalahan saat mengambil berita. Silakan coba lagi."
            }
        }
    }
}
