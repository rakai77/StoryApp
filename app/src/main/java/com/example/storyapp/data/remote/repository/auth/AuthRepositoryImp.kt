package com.example.storyapp.data.remote.repository.auth

import com.example.storyapp.data.Resource
import com.example.storyapp.data.remote.api.ApiService
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.RegisterResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(private val apiService: ApiService) : AuthRepository {

    override fun login(email: String, password: String): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading)
        try {
            val result = apiService.login(email, password)
            emit(Resource.Success(result))
        }  catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message.toString()))
        }
    }

    override fun register(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<RegisterResponse>> = flow {
        emit(Resource.Loading)
        try {
            val result = apiService.register(name, email, password)
            emit(Resource.Success(result))
        }  catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message.toString()))
        }
    }
}