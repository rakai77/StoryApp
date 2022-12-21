package com.example.storyapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.example.storyapp.MainDispatcherRule
import com.example.storyapp.PagingDataSource
import com.example.storyapp.adapter.ListStoryAdapter
import com.example.storyapp.data.remote.StoryRepository
import com.example.storyapp.data.remote.UserRepository
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.dummy.DataDummy
import com.example.storyapp.getOrAwaitValue
import com.example.storyapp.noopListUpdateCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var mainViewModel: MainViewModel
    @Mock private val userRepository= Mockito.mock(UserRepository::class.java)
    @Mock private val storyRepository = Mockito.mock(StoryRepository::class.java)

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Before
    fun setup() {
        mainViewModel = MainViewModel(userRepository, storyRepository)
    }

    @Test
    fun `get token successfully`() {
        val expectedToken = flowOf("S2xZnMbHw7RkGdYPirvWyN8Vf46TUpKs")
        Mockito.`when`(userRepository.getToken()).thenReturn(expectedToken)

        val actualToken = mainViewModel.getToken().getOrAwaitValue()
        Mockito.verify(userRepository).getToken()
        Assert.assertNotNull(actualToken)
        Assert.assertEquals("S2xZnMbHw7RkGdYPirvWyN8Vf46TUpKs", actualToken)
    }

    @Test
    fun `get session isLogin successfully`() {
        val expectedLogin = flowOf(true)
        Mockito.`when`(userRepository.isLogin()).thenReturn(expectedLogin)

        val actualLogin = mainViewModel.isLogin().getOrAwaitValue()
        Mockito.verify(userRepository).isLogin()
        Assert.assertNotNull(actualLogin)
        Assert.assertEquals(true, actualLogin)
    }

    @Test
    fun `set logout successfully`() = runTest {
        mainViewModel.logout()
        Mockito.verify(userRepository).logout()
    }

    @Test
    fun `when getStory should not null`() = runTest {
        val dummyListStory = DataDummy.generateDummyListStory()
        val data: PagingData<ListStoryItem> = PagingDataSource.snapshot(dummyListStory)
        val expectedListStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedListStory.value = data
        Mockito.`when`(storyRepository.getStories("S2xZnMbHw7RkGdYPirvWyN8Vf46TUpKs")).thenReturn(expectedListStory)

        val mainViewModel = MainViewModel(userRepository, storyRepository)
        val actualQuote: PagingData<ListStoryItem> =
            mainViewModel.getStories("S2xZnMbHw7RkGdYPirvWyN8Vf46TUpKs").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyListStory, differ.snapshot())
        Assert.assertEquals(dummyListStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyListStory[0].id, differ.snapshot()[0]?.id)
    }
}