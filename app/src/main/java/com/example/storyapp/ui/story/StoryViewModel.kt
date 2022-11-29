package com.example.storyapp.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.remote.StoryRepository
import com.example.storyapp.data.remote.UserRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getToken(): LiveData<String> {
        return userRepository.getToken().asLiveData()
    }

    fun addStory(token: String, imageMultipart: MultipartBody.Part, desc: RequestBody) =
        storyRepository.addNewStory(token, imageMultipart, desc)
}