package com.khesya.idn.newsapplication

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.khesya.idn.newsapplication.adapter.NewsAdapter
import com.khesya.idn.newsapplication.adapter.OnItemClickCallback
import com.khesya.idn.newsapplication.databinding.ActivityMainBinding
import com.khesya.idn.newsapplication.model.ArticlesItem
import com.khesya.idn.newsapplication.model.ResponseNews
import com.khesya.idn.newsapplication.service.RetrofitConfig
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.security.auth.callback.Callback

class MainActivity : AppCompatActivity(), View.OnClickListener {

    val date = getCurreDateTime()
    var refUsers : DatabaseReference? = null
    var firebaseUser : FirebaseUser? = null
    lateinit var mainBinding: ActivityMainBinding

    private fun getCurreDateTime(): Date {
        return  Calendar.getInstance().time
    }

    fun  Date.toString(format: String, locale: Locale = Locale.getDefault()):String{
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        supportActionBar?.hide()

        mainBinding.apply {
            ibProfile.setOnClickListener(this@MainActivity)
            tvDateMain.text = date.toString("dd/MM/yyyy")
        }
//        mainBinding.ibProfile.setOnClickListener(this)
//        mainBinding.tvDateMain.text = date.toString("dd/MM/yyyy")
        getNews()

    }

    private fun getNews() {
        val country = "id"
        val apikey = "47292931aae4422f85984b97d657002b"

        val loading = ProgressDialog.show(this, "Request Data", "Loading ..")
        RetrofitConfig.getInstance().getNewsHeadLines(country, apikey).enqueue(
            object : retrofit2.Callback<ResponseNews>{
                override fun onResponse(
                    call: Call<ResponseNews>,
                    response: Response<ResponseNews>
                ) {
                    Log.d ("Response", "Success" + response.body()?.articles)
                    loading.dismiss()
                    if (response.isSuccessful){
                        val status = response.body()?.status
                        if (status.equals("ok")){
                            Toast.makeText(this@MainActivity,
                                "Data Success!", Toast.LENGTH_SHORT
                            ).show()
                            val newsData = response.body()?.articles
                            val newsAdapter = NewsAdapter(this@MainActivity, newsData)
                            newsAdapter.setOnItemClickCallback(object: OnItemClickCallback{
                                override fun onItemClicked(news: ArticlesItem) {
                                    val intent = Intent(this@MainActivity, DetailActivity::class.java)
                                    intent.putExtra(DetailActivity.EXTRA_NEWS, news)
                                    startActivity(intent)
                                }
                            })
                            mainBinding.rvMain.apply {
                                adapter = newsAdapter
                                layoutManager = LinearLayoutManager(this@MainActivity)
                                val dataHighlight = response.body()
                                Glide.with(this@MainActivity)
                                    .load(dataHighlight?.articles?.component4()?.urlToImage)
                                    .centerCrop().into(mainBinding.ivHighlight)

                                mainBinding.apply {
                                    tvTitleHighlight.text = dataHighlight?.articles?.component4()?.title
                                    tvDateHighlight.text = dataHighlight?.articles?.component4()?.publishedAt
                                    tvAuthorNameHighlight.text = dataHighlight?.articles?.component4()?.author
                                }
                            }
                        }else{
                            Toast.makeText(this@MainActivity,
                                "Data Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseNews>, t: Throwable) {
                    Log.d("response", "Failed : " + t.localizedMessage)
                    loading.dismiss()
                }

            }
        )
    }

    companion object{
        fun getlaunchService(from: Context) = Intent(from, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    override fun onClick(p0: View) {
        when(p0.id){
            R.id.ibProfile -> startActivity(Intent(ProfileActivity.getLaunchService(this)))
        }
    }
}