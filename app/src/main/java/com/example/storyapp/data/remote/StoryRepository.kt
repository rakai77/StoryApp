package com.example.storyapp.data.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.example.storyapp.data.Resource
import com.example.storyapp.data.StoryRemoteMediator
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.api.ApiService
import com.example.storyapp.data.remote.response.AddNewStoryResponse
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun addNewStory(
        token: String,
        imageMultipart: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Resource<AddNewStoryResponse>> = liveData {
        emit(Resource.Loading)
        try {
            val client = apiService.addNewStory("Bearer $token", imageMultipart, description)
            emit(Resource.Success(client))
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("StoryRepository", "addNewStory: ${e.message.toString()}")
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun getLocation(token: String): LiveData<Resource<StoriesResponse>> = liveData {
        emit(Resource.Loading)
        try {
            val client = apiService.getStories("Bearer $token", location = 1)
            emit(Resource.Success(client))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null
        fun getInstance(
            storyDatabase: StoryDatabase,
            apiService: ApiService
        ): StoryRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: StoryRepository(storyDatabase, apiService)
        }.also { INSTANCE = it }
    }
}