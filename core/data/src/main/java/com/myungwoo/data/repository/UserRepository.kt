package com.myungwoo.data.repository

import com.myungwoo.datastore.UserPreferenceDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userPreferencesRepository: UserPreferenceDataSource
) {

    val email: Flow<String?> = userPreferencesRepository.email
    val password: Flow<String?> = userPreferencesRepository.password

    suspend fun saveUserLogin(email: String, password: String) {
        userPreferencesRepository.saveUserLogin(email, password)
    }

    suspend fun clearUserLogin() {
        userPreferencesRepository.clearUserLogin()
    }
}
