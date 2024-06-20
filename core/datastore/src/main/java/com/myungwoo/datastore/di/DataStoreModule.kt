package com.myungwoo.datastore.di

import android.content.Context
import com.myungwoo.datastore.UserPreferenceDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context
    ): UserPreferenceDataSource {
        return UserPreferenceDataSource(context)
    }
}