package com.myungwoo.shoppingmall_app.paging.network.di

import com.myungwoo.shoppingmall_app.paging.network.api.ApiService
import com.myungwoo.shoppingmall_app.paging.network.paging.PagingRepository
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
    fun provideApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.tvmaze.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDataRepository(apiService: ApiService): PagingRepository {
        return PagingRepository(apiService)
    }
}
