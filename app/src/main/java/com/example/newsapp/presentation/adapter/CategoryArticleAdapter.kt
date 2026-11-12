package com.example.newsapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.domain.model.Article

class CategoryArticleAdapter :
    RecyclerView.Adapter<CategoryArticleAdapter.CategoryArticleViewHolder>() {

    private val articles = mutableListOf<Article>()

    inner class CategoryArticleViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val image: ImageView =
            itemView.findViewById(R.id.ivCategoryArticle)

        val title: TextView =
            itemView.findViewById(R.id.tvCategoryArticleTitle)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_article, parent, false)

        return CategoryArticleViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CategoryArticleViewHolder,
        position: Int
    ) {
        val article = articles[position]

        holder.title.text = article.title

        Glide.with(holder.itemView.context)
            .load(article.urlToImage)
            .centerCrop()
            .into(holder.image)
    }

    override fun getItemCount(): Int = articles.size

    fun submitList(newArticles: List<Article>) {
        articles.clear()
        articles.addAll(newArticles)
        notifyDataSetChanged()
    }
}
