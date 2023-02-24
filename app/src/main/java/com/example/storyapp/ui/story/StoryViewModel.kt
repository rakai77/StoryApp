package com.example.storyapp.ui.story

import androidx.lifecycle.*
import com.example.storyapp.data.Resource

import com.example.storyapp.data.remote.repository.story.StoryRepository
import com.example.storyapp.data.remote.response.AddNewStoryResponse
import com.example.storyapp.utils.Preference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(private val storyRepository: StoryRepository, pref: Preference) : ViewModel() {

    private val _addStory = MutableLiveData<Resource<AddNewStoryResponse>>()
    val addStory: LiveData<Resource<AddNewStoryResponse>> = _addStory

    val token = pref.getToken()

    fun addStory(token: String, imageMultipart: MultipartBody.Part, desc: RequestBody) {
        storyRepository.addStories(token, imageMultipart, desc).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _addStory.value = Resource.Loading
                }
                is Resource.Success -> {
                    result.data.let {
                        _addStory.value = Resource.Success(it)
                    }
                }
                is Resource.Error -> {
                    _addStory.value = Resource.Error(result.error)
                }
            }
        }.launchIn(viewModelScope)
    }
}