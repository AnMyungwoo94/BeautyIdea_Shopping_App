package com.myungwoo.shoppingmall_app.network

import com.myungwoo.shoppingmall_app.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = BuildConfig.firebase_base_url

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
