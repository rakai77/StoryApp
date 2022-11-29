package com.example.storyapp.data.remote

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.storyapp.data.remote.api.ApiService
import com.example.storyapp.data.Resource
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.RegisterResponse
import com.example.storyapp.utils.Constanta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository private constructor(
    private val dataStore: DataStore<Preferences>,
    private val apiService: ApiService
) {

    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Resource<RegisterResponse>> = liveData {
        emit(Resource.Loading)
        try {
            val result = apiService.register(name, email, password)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun login(email: String, password: String): LiveData<Resource<LoginResponse>> = liveData {
        emit(Resource.Loading)
        try {
            val result = apiService.login(email, password)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preference ->
            preference[Constanta.TOKEN] ?: ""
        }
    }

    suspend fun setToken(token: String, isLogin: Boolean) {
        dataStore.edit { preference ->
            preference[Constanta.TOKEN] = token
            preference[Constanta.STATE] = isLogin
        }
    }

    fun isLogin(): Flow<Boolean> {
        return dataStore.data.map { preference ->
            preference[Constanta.STATE] ?: false
        }
    }

    suspend fun logout() {
        dataStore.edit { preference ->
            preference[Constanta.TOKEN] = ""
            preference[Constanta.STATE] = false
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null
        fun getInstance(dataStore: DataStore<Preferences>, apiService: ApiService): UserRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = UserRepository(dataStore, apiService)
                INSTANCE = instance
                instance
            }
        }
    }
}