package com.example.storyapp.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.data.remote.response.ListStoryItem

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(storyItem: List<StoryEntity>)

    @Query("SELECT * FROM story_entity")
    fun getAllStory(): PagingSource<Int, StoryEntity>

    @Query("DELETE FROM story_entity")
    suspend fun deleteAll()

}