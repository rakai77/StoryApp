package com.example.storyapp.data.remote.repository.story

import androidx.paging.PagingData
import com.example.storyapp.data.Resource
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.data.remote.response.AddNewStoryResponse
import com.example.storyapp.data.remote.response.DetailResponse
import com.example.storyapp.data.remote.response.StoriesResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface StoryRepository {

    fun addStories(token: String, imageMultipart: MultipartBody.Part, description: RequestBody) : Flow<Resource<AddNewStoryResponse>>

    fun getAllStory(token: String) : Flow<PagingData<StoryEntity>>

    fun getStoryWithLocation(token: String) : Flow<Resource<StoriesResponse>>
}