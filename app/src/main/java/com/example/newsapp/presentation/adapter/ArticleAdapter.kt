
package com.example.newsapp.presentation.adapter

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

    inner class ArticleViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val ivArticleImage: ImageView =
            itemView.findViewById(R.id.ivArticleImage)

        private val tvArticleTitle: TextView =
            itemView.findViewById(R.id.tvArticleTitle)

        private val tvArticleDescription: TextView =
            itemView.findViewById(R.id.tvArticleDescription)

        private val lottieImageLoading: LottieAnimationView =
            itemView.findViewById(R.id.lottieImageLoading)

        private val tvImageError: TextView =
            itemView.findViewById(R.id.tvImageError)

        fun bind(article: Article) {
            tvArticleTitle.text =
                article.title.ifBlank { "Judul tidak tersedia" }

            tvArticleDescription.text =
                article.description
                    ?.takeIf { it.isNotBlank() }
                    ?: "Deskripsi tidak tersedia"

            tvImageError.visibility = View.GONE

            val imageUrl = article.urlToImage

            if (imageUrl.isNullOrBlank()) {
                Glide.with(ivArticleImage).clear(ivArticleImage)
                lottieImageLoading.cancelAnimation()
                lottieImageLoading.visibility = View.GONE
                ivArticleImage.setImageResource(
                    android.R.drawable.ic_menu_gallery
                )
                tvImageError.visibility = View.VISIBLE
                return
            }

            lottieImageLoading.visibility = View.VISIBLE
            lottieImageLoading.repeatCount =
                com.airbnb.lottie.LottieDrawable.INFINITE
            lottieImageLoading.playAnimation()

            Glide.with(ivArticleImage)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .listener(object : RequestListener<android.graphics.drawable.Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<android.graphics.drawable.Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        lottieImageLoading.cancelAnimation()
                        lottieImageLoading.visibility = View.GONE
                        tvImageError.visibility = View.VISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: android.graphics.drawable.Drawable,
                        model: Any,
                        target: Target<android.graphics.drawable.Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        lottieImageLoading.cancelAnimation()
                        lottieImageLoading.visibility = View.GONE
                        tvImageError.visibility = View.GONE
                        return false
                    }
                })
                .into(ivArticleImage)

            itemView.setOnClickListener {
                onArticleClick(article)
            }
        }
    }
}
