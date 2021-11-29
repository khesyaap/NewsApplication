package com.khesya.idn.newsapplication.service

import com.khesya.idn.newsapplication.model.ResponseNews
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines")
    fun getNewsHeadLines(
        @Query("country") country: String?,
        @Query("apiKey") apiKey : String?
    ): Call<ResponseNews>
}