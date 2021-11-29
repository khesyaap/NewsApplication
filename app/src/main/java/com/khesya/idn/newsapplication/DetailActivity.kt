package com.khesya.idn.newsapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.khesya.idn.newsapplication.databinding.ActivityDetail2Binding
import com.khesya.idn.newsapplication.model.ArticlesItem

class DetailActivity : AppCompatActivity() {
    private lateinit var detailBinding : ActivityDetail2Binding
    private lateinit var news : ArticlesItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailBinding = ActivityDetail2Binding.inflate(layoutInflater)
        setContentView(detailBinding.root)
        news = intent.getParcelableExtra<ArticlesItem>(EXTRA_NEWS) as ArticlesItem
        supportActionBar?.hide()
        showDetailData()
    }

    private fun showDetailData() {
        detailBinding.apply {
            tvDetailTitle.text = news.title
            tvNameDetail.text = news.author
            tvDateDetail.text = news.publishedAt
            tvDescDetail.text = news.description
            tvContentDetail.text = news.content

            Glide.with(this@DetailActivity)
                .load(news.urlToImage).into(detailBinding.ivDetail)
        }
    }

    companion object{
        const val EXTRA_NEWS =  "extra_news"
    }
}