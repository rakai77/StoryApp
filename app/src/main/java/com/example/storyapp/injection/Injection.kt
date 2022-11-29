package com.example.storyapp.injection

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.StoryRepository
import com.example.storyapp.data.remote.UserRepository
import com.example.storyapp.data.remote.api.ApiConfig

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "setting")

object Injection {

    fun provideUserRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(context.dataStore, apiService)
    }

    fun provideStoryRepository(context: Context) : StoryRepository {
        val apiService = ApiConfig.getApiService()
        val database  = StoryDatabase.getDatabase(context)
        return StoryRepository(database, apiService)
    }
}