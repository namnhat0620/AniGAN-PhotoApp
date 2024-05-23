package com.kltn.anigan.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

object DataStoreManager {

    private val USERNAME = stringPreferencesKey("username")
    private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    private val NUMBER_OF_GENERATION = stringPreferencesKey("number_of_generation")

    suspend fun saveUsername(context: Context, username: String) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME] = username
        }
    }

    fun getUsername(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USERNAME]
        }
    }

    suspend fun clearUsername(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.remove(USERNAME)
        }
    }

    suspend fun saveRefreshToken(context: Context, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    fun getRefreshToken(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN]
        }
    }

    suspend fun clearRefreshToken(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.remove(REFRESH_TOKEN)
        }
    }

    suspend fun saveNoOfGeneration(context: Context, noOfGeneration: String) {
        context.dataStore.edit { preferences ->
            preferences[NUMBER_OF_GENERATION] = noOfGeneration
        }
    }

    fun getNoOfGeneration(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[NUMBER_OF_GENERATION]
        }
    }

    suspend fun clearNoOfGeneration(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.remove(NUMBER_OF_GENERATION)
        }
    }
}
