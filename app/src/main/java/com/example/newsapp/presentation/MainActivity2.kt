package com.example.newsapp.presentation

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.PagerSnapHelper
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
    private lateinit var layoutManager: LinearLayoutManager

    private val newsViewModel
        get() = (application as MyApplication)
            .appContainer.newsViewModel

    private val snapHelper = PagerSnapHelper()

    private var autoScrollJob: Job? = null
    private var userIsDragging = false
    private var direction = 1

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

        layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        rvMainNews.layoutManager = layoutManager
        rvMainNews.adapter = articleAdapter

        snapHelper.attachToRecyclerView(rvMainNews)

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

                                lifecycleScope.launch {
                                    delay(400)
                                    startAutoScroll()
                                }
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
                            direction = 1
                            rvMainNews.scrollToPosition(0)

                            rvMainNews.post {
                                startAutoScroll()
                            }
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
            if (loading && articles.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }

        layoutNewsError.visibility =
            if (!loading && !error.isNullOrBlank()) {
                View.VISIBLE
            } else {
                View.GONE
            }

        rvMainNews.visibility =
            if (articles.isNotEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }

        if (loading && articles.isEmpty()) {
            tvNewsCount.text = "Memuat berita..."
        }
    }

    


private fun startAutoScroll() {

    if (autoScrollJob?.isActive == true) return
    if (!::articleAdapter.isInitialized) return
    if (articleAdapter.itemCount < 2) return

    autoScrollJob = lifecycleScope.launch {

        while (isActive) {

            if (userIsDragging || !rvMainNews.isShown) {
                delay(100)
                continue
            }

            val lastPosition = articleAdapter.itemCount - 1

            val snapView = snapHelper.findSnapView(layoutManager)

            val currentPosition = snapView?.let {
                layoutManager.getPosition(it)
            } ?: layoutManager.findFirstVisibleItemPosition()

            if (currentPosition == RecyclerView.NO_POSITION) {
                delay(100)
                continue
            }

            // Kartu terakhir:
            // Jeda 3 detik sudah diberikan setelah
            // animasi maju selesai. Cukup balik arah.
            if (currentPosition >= lastPosition && direction > 0) {
                direction = -1
            }

            // Kartu pertama:
            // Beri jeda 3 detik sebelum bergerak maju lagi.
            if (currentPosition <= 0 && direction < 0) {

                delay(3000)

                if (userIsDragging) continue

                direction = 1
            }

            val targetPosition = currentPosition + direction

            if (targetPosition !in 0..lastPosition) {
                continue
            }

            val duration = if (direction > 0) {
                500
            } else {
                50
            }

            smoothScrollToPosition(targetPosition, duration)

            // Tunggu sampai animasi selesai dan kartu
            // tujuan berada pada posisi snap.
            while (isActive) {

                val snappedView =
                    snapHelper.findSnapView(layoutManager)

                val snappedPosition = snappedView?.let {
                    layoutManager.getPosition(it)
                }

                if (
                    rvMainNews.scrollState == RecyclerView.SCROLL_STATE_IDLE &&
                    snappedPosition == targetPosition
                ) {
                    break
                }

                if (userIsDragging) break

                delay(16)
            }

            // Jeda baca 3 detik setelah setiap
            // perpindahan maju, termasuk kartu terakhir.
            // Tidak ada jeda setelah perpindahan mundur.
            if (direction > 0 && !userIsDragging) {
                delay(3000)
            }
        }
    }
}




    private fun smoothScrollToPosition(
        position: Int,
        duration: Int
    ) {

        val smoothScroller =
            object : LinearSmoothScroller(this) {

                override fun calculateTimeForScrolling(
                    dx: Int
                ): Int {
                    return duration
                }

                override fun calculateTimeForDeceleration(
                    dx: Int
                ): Int {
                    return duration
                }

                override fun calculateSpeedPerPixel(
                    displayMetrics: DisplayMetrics
                ): Float {
                    return 1f
                }
            }

        smoothScroller.targetPosition = position
        layoutManager.startSmoothScroll(smoothScroller)
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
