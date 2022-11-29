package com.example.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.remote.StoryRepository
import com.example.storyapp.data.remote.UserRepository

class MapsViewModel(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    fun getStoryLocation(token: String) = storyRepository.getLocation(token)

    fun getToken() : LiveData<String> {
        return userRepository.getToken().asLiveData()
    }
}