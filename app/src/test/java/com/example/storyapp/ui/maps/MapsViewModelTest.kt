package com.example.storyapp.ui.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.MainDispatcherRule
import com.example.storyapp.data.Resource
import com.example.storyapp.data.remote.StoryRepository
import com.example.storyapp.data.remote.UserRepository
import com.example.storyapp.data.remote.response.StoriesResponse
import com.example.storyapp.dummy.DataDummy
import com.example.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class MapsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var mapsViewModel: MapsViewModel

    @Mock private val userRepository= Mockito.mock(UserRepository::class.java)
    @Mock private val storyRepository = Mockito.mock(StoryRepository::class.java)
    private var dummyMaps = DataDummy.generateDummyStoryResponse()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Before
    fun setup() {
        mapsViewModel = MapsViewModel(storyRepository,userRepository)
    }

    @Test
    fun `when getStoryLocation() is called and return success`() {
        val expectedLocation = MutableLiveData<Resource<StoriesResponse>>()
        expectedLocation.value = Resource.Success(dummyMaps)
        Mockito.`when`(storyRepository.getLocation("S2xZnMbHw7RkGdYPirvWyN8Vf46TUpKs"))
            .thenReturn(expectedLocation)

        val actualMapsLocation =
            mapsViewModel.getStoryLocation("S2xZnMbHw7RkGdYPirvWyN8Vf46TUpKs").getOrAwaitValue()
        Assert.assertNotNull(actualMapsLocation)
        Assert.assertTrue(actualMapsLocation is Resource.Success)
        Assert.assertEquals(dummyMaps, (actualMapsLocation as Resource.Success).data)
    }

    @Test
    fun `when getStoryLocation() is failed and return error`() {
        val expectedLocation = MutableLiveData<Resource<StoriesResponse>>()
        expectedLocation.value = Resource.Error("Error")
        Mockito.`when`(storyRepository.getLocation("S2xZnMbHw7RkGdYPirvWyN8Vf46TUpKs")).thenReturn(expectedLocation)

        val actualStory = mapsViewModel.getStoryLocation("S2xZnMbHw7RkGdYPirvWyN8Vf46TUpKs").getOrAwaitValue()
        Assert.assertNotNull(actualStory)
        Assert.assertTrue(actualStory is Resource.Error)
    }

    @Test
    fun `get token successfully`() {
        val expectedToken = flowOf("S2xZnMbHw7RkGdYPirvWyN8Vf46TUpKs")
        Mockito.`when`(userRepository.getToken()).thenReturn(expectedToken)

        val actualToken = mapsViewModel.getToken().getOrAwaitValue()
        Mockito.verify(userRepository).getToken()
        Assert.assertNotNull(actualToken)
        Assert.assertEquals("S2xZnMbHw7RkGdYPirvWyN8Vf46TUpKs", actualToken)
    }
}