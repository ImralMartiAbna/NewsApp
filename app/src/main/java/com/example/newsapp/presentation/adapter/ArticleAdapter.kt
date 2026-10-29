package com.example.newsapp.presentation.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.newsapp.R
import com.example.newsapp.domain.model.Article

class ArticleAdapter(
    private val onArticleClick: (Article) -> Unit = {}
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private val articles = mutableListOf<Article>()

    fun updateArticles(newArticles: List<Article>) {
        articles.clear()
        articles.addAll(newArticles)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)

        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ArticleViewHolder,
        position: Int
    ) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    override fun onViewRecycled(holder: ArticleViewHolder) {
        holder.clearImageRequest()
        super.onViewRecycled(holder)
    }

    inner class ArticleViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val ivArticleImage: ImageView =
            itemView.findViewById(R.id.ivArticleImage)

        private val tvArticleTitle: TextView =
            itemView.findViewById(R.id.tvArticleTitle)

        private val lottieImageLoading: LottieAnimationView =
            itemView.findViewById(R.id.lottieImageLoading)

        private val tvImageError: TextView =
            itemView.findViewById(R.id.tvImageError)

        init {
            lottieImageLoading.cancelAnimation()
            lottieImageLoading.visibility = View.GONE
        }

        fun bind(article: Article) {
            itemView.setOnClickListener {
                onArticleClick(article)
            }

            // Kartu hanya menampilkan judul, tanpa deskripsi.
            tvArticleTitle.text =
                article.title.ifBlank { "Judul tidak tersedia" }

            tvImageError.visibility = View.GONE
            clearImageRequest()

            val imageUrl = article.urlToImage

            if (imageUrl.isNullOrBlank()) {
                ivArticleImage.setImageResource(
                    android.R.drawable.ic_menu_gallery
                )
                tvImageError.visibility = View.VISIBLE
                return
            }

            Glide.with(ivArticleImage)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .listener(object : RequestListener<Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        stopLoadingAnimation()
                        tvImageError.visibility = View.VISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        stopLoadingAnimation()
                        tvImageError.visibility = View.GONE
                        return false
                    }
                })
                .into(ivArticleImage)
        }

        private fun stopLoadingAnimation() {
            lottieImageLoading.cancelAnimation()
            lottieImageLoading.visibility = View.GONE
        }

        fun clearImageRequest() {
            Glide.with(ivArticleImage).clear(ivArticleImage)
            lottieImageLoading.cancelAnimation()
            lottieImageLoading.visibility = View.GONE
        }
    }
}