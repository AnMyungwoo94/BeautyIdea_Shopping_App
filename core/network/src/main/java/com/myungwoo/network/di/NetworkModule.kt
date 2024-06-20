package com.myungwoo.network.di

import com.myungwoo.network.BuildConfig
import com.myungwoo.network.adapter.ResultCallAdapter
import com.myungwoo.network.api.FirebaseApi
import com.myungwoo.network.interceptors.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.firebase_base_url)
            .client(client)
            .addCallAdapterFactory(ResultCallAdapter.Factory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFirebaseApi(retrofit: Retrofit): FirebaseApi {
        return retrofit.create(FirebaseApi::class.java)
    }
}