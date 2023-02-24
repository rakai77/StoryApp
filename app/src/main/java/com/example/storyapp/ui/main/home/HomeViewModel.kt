package com.example.storyapp.ui.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.storyapp.data.remote.repository.story.StoryRepository
import com.example.storyapp.utils.Preference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repo: StoryRepository, pref: Preference) : ViewModel() {

    val token = pref.getToken()

    fun getALlStory(token: String) = repo.getAllStory(token).cachedIn(viewModelScope)

}