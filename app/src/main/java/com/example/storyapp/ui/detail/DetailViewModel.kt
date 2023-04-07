package com.example.storyapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.Resource
import com.example.storyapp.data.remote.repository.story.StoryRepository
import com.example.storyapp.data.remote.response.DetailStoryResponse
import com.example.storyapp.utils.Preference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repo: StoryRepository, private val pref: Preference) : ViewModel() {

    private val _detailStory = MutableLiveData<Resource<DetailStoryResponse>>()
    val detailStory: LiveData<Resource<DetailStoryResponse>> = _detailStory

    val getToken = pref.getToken()

    fun getDetailStories(token: String, id: String) {
        repo.getDetailStories(token, id).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _detailStory.value = Resource.Loading
                }
                is Resource.Success -> {
                    result.data.let {
                        _detailStory.value = Resource.Success(it)
                    }
                }
                is Resource.Error -> {
                    _detailStory.value = Resource.Error(result.error)
                }
            }
        }.launchIn(viewModelScope)
    }
}