package com.example.storyapp.injection

import android.content.Context
import androidx.room.Room
import com.example.storyapp.data.local.room.StoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideProductDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        StoryDatabase::class.java,
        "story_db"
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideStoryDao(database: StoryDatabase) = database.storyDao()

    @Singleton
    @Provides
    fun provideRemoteKeysDao(database: StoryDatabase) = database.remoteKeysDao()
}