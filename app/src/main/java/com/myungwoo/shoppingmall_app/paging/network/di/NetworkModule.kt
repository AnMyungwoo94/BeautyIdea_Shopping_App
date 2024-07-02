package com.myungwoo.shoppingmall_app.paging.network.di

import com.myungwoo.shoppingmall_app.paging.network.api.BeautyIdeaApiService
import com.myungwoo.shoppingmall_app.paging.network.repository.NetworkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(): BeautyIdeaApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.tvmaze.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BeautyIdeaApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideApiRepository(apiService: BeautyIdeaApiService): NetworkRepository {
        return NetworkRepository(apiService)
    }
}
