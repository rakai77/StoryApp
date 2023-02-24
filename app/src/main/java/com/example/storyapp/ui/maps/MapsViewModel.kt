package com.example.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.Resource
import com.example.storyapp.data.remote.repository.story.StoryRepository
import com.example.storyapp.data.remote.response.StoriesResponse
import com.example.storyapp.utils.Preference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(private val repo: StoryRepository, pref: Preference) : ViewModel() {

    private val _maps = MutableLiveData<Resource<StoriesResponse>>()
    val maps: LiveData<Resource<StoriesResponse>> = _maps

    val token = pref.getToken()

    fun getStoryLocation(token: String) {
        repo.getStoryWithLocation(token).onEach { result ->
            when(result) {
                is Resource.Loading -> {
                    _maps.value = Resource.Loading
                }
                is Resource.Success -> {
                    result.data.let {
                        _maps.value = Resource.Success(it)
                    }
                }
                is Resource.Error -> {
                    _maps.value = Resource.Error(result.error)
                }
            }
        }.launchIn(viewModelScope)
    }
}