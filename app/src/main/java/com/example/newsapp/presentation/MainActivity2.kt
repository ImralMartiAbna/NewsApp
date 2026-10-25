
package com.example.newsapp.presentation

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.MyApplication
import com.example.newsapp.R
import com.example.newsapp.domain.model.Article
import com.example.newsapp.presentation.adapter.ArticleAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity2 : AppCompatActivity() {

    private lateinit var rvMainNews: RecyclerView
    private lateinit var tvNewsCount: TextView
    private lateinit var layoutNewsLoading: LinearLayout
    private lateinit var layoutNewsError: LinearLayout
    private lateinit var tvNewsError: TextView
    private lateinit var btnRetryNews: Button

    private lateinit var articleAdapter: ArticleAdapter

    private val newsViewModel
        get() = (application as MyApplication)
            .appContainer.newsViewModel

    private var autoScrollJob: Job? = null
    private var userIsDragging = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        rvMainNews = findViewById(R.id.rvMainNews)
        tvNewsCount = findViewById(R.id.tvNewsCount)
        layoutNewsLoading = findViewById(R.id.layoutNewsLoading)
        layoutNewsError = findViewById(R.id.layoutNewsError)
        tvNewsError = findViewById(R.id.tvNewsError)
        btnRetryNews = findViewById(R.id.btnRetryNews)

        articleAdapter = ArticleAdapter { article ->
            openArticleDetail(article)
        }

        rvMainNews.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rvMainNews.adapter = articleAdapter

        rvMainNews.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(
                    recyclerView: RecyclerView,
                    newState: Int
                ) {
                    when (newState) {
                        RecyclerView.SCROLL_STATE_DRAGGING -> {
                            userIsDragging = true
                            stopAutoScroll()
                        }

                        RecyclerView.SCROLL_STATE_IDLE -> {
                            if (userIsDragging) {
                                userIsDragging = false
                                startAutoScroll()
                            }
                        }
                    }
                }
            }
        )

        btnRetryNews.setOnClickListener {
            newsViewModel.getTopHeadlines("us")
        }

        observeNews()
    }

    private fun observeNews() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    newsViewModel.articles.collect { articles ->
                        articleAdapter.updateArticles(articles)
                        tvNewsCount.text =
                            if (articles.isEmpty()) {
                                "Belum ada berita"
                            } else {
                                "${articles.size} berita tersedia"
                            }

                        if (articles.isNotEmpty()) {
                            startAutoScroll()
                        } else {
                            stopAutoScroll()
                        }

                        updateNewsState()
                    }
                }

                launch {
                    newsViewModel.isLoading.collect {
                        updateNewsState()
                    }
                }

                launch {
                    newsViewModel.errorMessage.collect { message ->
                        if (!message.isNullOrBlank()) {
                            tvNewsError.text = message
                        }
                        updateNewsState()
                    }
                }
            }
        }
    }

    private fun updateNewsState() {
        val loading = newsViewModel.isLoading.value
        val error = newsViewModel.errorMessage.value
        val articles = newsViewModel.articles.value

        layoutNewsLoading.visibility =
            if (loading && articles.isEmpty()) View.VISIBLE
            else View.GONE

        layoutNewsError.visibility =
            if (!loading && !error.isNullOrBlank()) View.VISIBLE
            else View.GONE

        rvMainNews.visibility =
            if (articles.isNotEmpty()) View.VISIBLE
            else View.GONE

        if (loading && articles.isEmpty()) {
            tvNewsCount.text = "Memuat berita..."
        }
    }

    private fun startAutoScroll() {
        if (autoScrollJob?.isActive == true) return
        if (!::articleAdapter.isInitialized) return
        if (articleAdapter.itemCount < 2) return

        autoScrollJob = lifecycleScope.launch {
            var direction = 1

            while (isActive) {
                delay(35)

                if (userIsDragging || !rvMainNews.isShown) {
                    continue
                }

                val layoutManager =
                    rvMainNews.layoutManager as? LinearLayoutManager
                        ?: continue

                val lastPosition =
                    layoutManager.findLastCompletelyVisibleItemPosition()

                val firstPosition =
                    layoutManager.findFirstCompletelyVisibleItemPosition()

                if (
                    direction > 0 &&
                    lastPosition >= articleAdapter.itemCount - 1
                ) {
                    direction = -1
                } else if (direction < 0 && firstPosition <= 0) {
                    direction = 1
                }

                rvMainNews.scrollBy(direction * 2, 0)
            }
        }
    }

    private fun stopAutoScroll() {
        autoScrollJob?.cancel()
        autoScrollJob = null
    }

    private fun openArticleDetail(article: Article) {
        // Navigasi ke NewsDetailActivity akan ditambahkan
        // setelah file NewsDetailActivity.kt dibuat.
    }

    override fun onResume() {
        super.onResume()
        startAutoScroll()
    }

    override fun onPause() {
        stopAutoScroll()
        super.onPause()
    }
}
