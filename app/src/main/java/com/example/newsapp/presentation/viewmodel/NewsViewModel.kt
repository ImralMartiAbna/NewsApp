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

    // =====================================
    // BERITA UTAMA
    // =====================================

    private val _articles =
        MutableStateFlow<List<Article>>(emptyList())

    val articles: StateFlow<List<Article>> = _articles

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> = _isLoading


    // =====================================
    // BERITA TEKNOLOGI
    // =====================================

    private val _technologyArticles =
        MutableStateFlow<List<Article>>(emptyList())

    val technologyArticles: StateFlow<List<Article>> =
        _technologyArticles

    private val _technologyLoading =
        MutableStateFlow(false)

    val technologyLoading: StateFlow<Boolean> =
        _technologyLoading

    private val _technologyError =
        MutableStateFlow<String?>(null)

    val technologyError: StateFlow<String?> =
        _technologyError


    // =====================================
    // BERITA BISNIS
    // =====================================

    private val _businessArticles =
        MutableStateFlow<List<Article>>(emptyList())

    val businessArticles: StateFlow<List<Article>> =
        _businessArticles

    private val _businessLoading =
        MutableStateFlow(false)

    val businessLoading: StateFlow<Boolean> =
        _businessLoading

    private val _businessError =
        MutableStateFlow<String?>(null)

    val businessError: StateFlow<String?> =
        _businessError


    // =====================================
    // BERITA OLAHRAGA
    // =====================================

    private val _sportsArticles =
        MutableStateFlow<List<Article>>(emptyList())

    val sportsArticles: StateFlow<List<Article>> =
        _sportsArticles

    private val _sportsLoading =
        MutableStateFlow(false)

    val sportsLoading: StateFlow<Boolean> =
        _sportsLoading

    private val _sportsError =
        MutableStateFlow<String?>(null)

    val sportsError: StateFlow<String?> =
        _sportsError


    // =====================================
    // BERITA KESEHATAN
    // =====================================

    private val _healthArticles =
        MutableStateFlow<List<Article>>(emptyList())

    val healthArticles: StateFlow<List<Article>> =
        _healthArticles

    private val _healthLoading =
        MutableStateFlow(false)

    val healthLoading: StateFlow<Boolean> =
        _healthLoading

    private val _healthError =
        MutableStateFlow<String?>(null)

    val healthError: StateFlow<String?> =
        _healthError


    // =====================================
    // MENGAMBIL BERITA UTAMA
    // =====================================

    fun getTopHeadlines(country: String) {

        viewModelScope.launch {

            _errorMessage.value = null
            _isLoading.value = true

            try {

                _articles.value =
                    newsUseCase.getTopHeadlines(country)

            } catch (e: CancellationException) {
                throw e

            } catch (e: HttpException) {

                val response = e.response()

                val body = runCatching {
                    response?.errorBody()?.string().orEmpty()
                }.getOrDefault("")

                val headers = response?.headers()

                val contentType =
                    headers?.get("Content-Type").orEmpty()

                val server =
                    headers?.get("Server").orEmpty()

                val via =
                    headers?.get("Via").orEmpty()

                val json = runCatching {
                    JSONObject(body)
                }.getOrNull()

                val code = json?.optString("code").orEmpty()

                val message =
                    json?.optString("message").orEmpty()

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

            } finally {

                _isLoading.value = false
            }
        }
    }


    // =====================================
    // FUNGSI BERITA KATEGORI
    // =====================================

    fun getTechnologyNews(country: String = "us") {
        loadCategoryNews(
            country,
            "technology",
            _technologyArticles,
            _technologyLoading,
            _technologyError
        )
    }

    fun getBusinessNews(country: String = "us") {
        loadCategoryNews(
            country,
            "business",
            _businessArticles,
            _businessLoading,
            _businessError
        )
    }

    fun getSportsNews(country: String = "us") {
        loadCategoryNews(
            country,
            "sports",
            _sportsArticles,
            _sportsLoading,
            _sportsError
        )
    }

    fun getHealthNews(country: String = "us") {
        loadCategoryNews(
            country,
            "health",
            _healthArticles,
            _healthLoading,
            _healthError
        )
    }


    // =====================================
    // PROSES PENGAMBILAN KATEGORI
    // =====================================

    private fun loadCategoryNews(
        country: String,
        category: String,
        articles: MutableStateFlow<List<Article>>,
        loading: MutableStateFlow<Boolean>,
        error: MutableStateFlow<String?>
    ) {

        viewModelScope.launch {

            error.value = null
            loading.value = true

            try {

                articles.value =
                    newsUseCase.getTopHeadlines(
                        country = country,
                        category = category
                    )

            } catch (e: CancellationException) {

                throw e

            } catch (e: HttpException) {

                error.value =
                    "Gagal memuat berita $category (HTTP ${e.code()})."

            } catch (e: IOException) {

                error.value =
                    "Koneksi internet bermasalah saat memuat berita $category."

            } catch (e: Exception) {

                error.value =
                    "Terjadi kesalahan saat memuat berita $category."

            } finally {

                loading.value = false
            }
        }
    }
}
