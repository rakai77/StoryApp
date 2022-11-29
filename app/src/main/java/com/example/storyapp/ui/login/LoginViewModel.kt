package com.example.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    fun getToken() : LiveData<String> {
        return repository.getToken().asLiveData()
    }

    fun setToken(token: String, isLogin: Boolean) {
        viewModelScope.launch {
            repository.setToken(token, isLogin)
        }
    }

    fun login(email: String, password: String) = repository.login(email, password)
}