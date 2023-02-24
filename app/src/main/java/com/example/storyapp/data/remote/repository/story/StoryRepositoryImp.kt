package com.example.storyapp.data.remote.repository.story

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.storyapp.data.Resource
import com.example.storyapp.data.remote.repository.StoryRemoteMediator
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.api.ApiService
import com.example.storyapp.data.remote.response.AddNewStoryResponse
import com.example.storyapp.data.remote.response.DetailResponse
import com.example.storyapp.data.remote.response.StoriesResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class StoryRepositoryImp @Inject constructor(private val apiService: ApiService, private val database: StoryDatabase) : StoryRepository {

    override fun addStories(
        token: String,
        imageMultipart: MultipartBody.Part,
        description: RequestBody
    ): Flow<Resource<AddNewStoryResponse>> = flow {
        emit(Resource.Loading)
        try {
            val result = apiService.addNewStory(token, imageMultipart, description)
            emit(Resource.Success(result))
        }  catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message.toString()))
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllStory(token: String): Flow<PagingData<StoryEntity>> {
        val pagingSourceFactory = { database.storyDao().getAllStory() }
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(
                apiService = apiService,
                database = database,
                token = token
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun getStoryWithLocation(token: String): Flow<Resource<StoriesResponse>> = flow {
        emit(Resource.Loading)
        try {
            val result = apiService.getStories(token, location = 1)
            emit(Resource.Success(result))
        }  catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message.toString()))
        }
    }
}