package com.example.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.StoryRepository
import com.example.storyapp.data.remote.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getToken(): LiveData<String> {
        return userRepository.getToken().asLiveData()
    }

    fun getStories(token: String) = storyRepository.getStories(token)

    fun isLogin(): LiveData<Boolean> {
        return userRepository.isLogin().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}