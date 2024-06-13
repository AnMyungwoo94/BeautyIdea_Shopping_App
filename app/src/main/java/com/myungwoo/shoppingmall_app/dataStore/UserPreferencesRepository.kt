package com.myungwoo.shoppingmall_app.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    val email: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[EMAIL_KEY]
        }

    val password: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PASSWORD_KEY]
        }

    suspend fun saveUserLogin(email: String, password: String) {
        context.dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = email
            preferences[PASSWORD_KEY] = password
        }
    }

    suspend fun clearUserLogin() {
        context.dataStore.edit { preferences ->
            preferences.remove(EMAIL_KEY)
            preferences.remove(PASSWORD_KEY)
        }
    }

    companion object {
        val EMAIL_KEY = stringPreferencesKey("user_email")
        val PASSWORD_KEY = stringPreferencesKey("user_password")
    }
}