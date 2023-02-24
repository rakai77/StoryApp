package com.example.storyapp.data.remote.repository.auth

import com.example.storyapp.data.Resource
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.RegisterResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun login(email: String, password: String): Flow<Resource<LoginResponse>>

    fun register(name: String, email: String, password: String) : Flow<Resource<RegisterResponse>>
}