package com.example.storyapp.ui.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.MainDispatcherRule
import com.example.storyapp.data.Resource
import com.example.storyapp.data.remote.StoryRepository
import com.example.storyapp.data.remote.UserRepository
import com.example.storyapp.data.remote.response.AddNewStoryResponse
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
class StoryViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var storyViewModel: StoryViewModel
    @Mock private val userRepository= Mockito.mock(UserRepository::class.java)
    @Mock private val storyRepository = Mockito.mock(StoryRepository::class.java)
    private var dummyMultipart = DataDummy.generateDummyMultipartFile()
    private var dummyDescription = DataDummy.generateDummyRequestBody()
    private val dummyResponse = DataDummy.generateDummyAddNewStoryResponse()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Before
    fun setup() {
        storyViewModel = StoryViewModel(userRepository,storyRepository)
    }

    @Test
    fun `when addStory is called and return success`() {
        val expectedUploadStory = MutableLiveData<Resource<AddNewStoryResponse>>()
        expectedUploadStory.value = Resource.Success(dummyResponse)
        Mockito.`when`(storyRepository.addNewStory("token", dummyMultipart, dummyDescription))
            .thenReturn(
                expectedUploadStory
            )
        val actualUpload = storyViewModel.addStory("token", dummyMultipart, dummyDescription).getOrAwaitValue()
        Mockito.verify(storyRepository).addNewStory("token", dummyMultipart, dummyDescription)
        Assert.assertNotNull(actualUpload)
        Assert.assertTrue(actualUpload is Resource.Success)
        Assert.assertEquals(dummyResponse, (actualUpload as Resource.Success).data)
    }

    @Test
    fun `when addStory is failed and return error`() {
        val expectedUploadStory = MutableLiveData<Resource<AddNewStoryResponse>>()
        expectedUploadStory.value = Resource.Error("error")
        Mockito.`when`(storyRepository.addNewStory("token", dummyMultipart, dummyDescription))
            .thenReturn(
                expectedUploadStory
            )
        val actualUpload = storyViewModel.addStory("token", dummyMultipart, dummyDescription).getOrAwaitValue()
        Mockito.verify(storyRepository).addNewStory("token", dummyMultipart, dummyDescription)
        Assert.assertNotNull(actualUpload)
        Assert.assertTrue(actualUpload is Resource.Error)
    }

    @Test
    fun `get token successfully`() {
        val expectedToken = flowOf("S2xZnMbHw7RkGdYPirvWyN8Vf46TUpKs")
        Mockito.`when`(userRepository.getToken()).thenReturn(expectedToken)

        val actualToken = storyViewModel.getToken().getOrAwaitValue()
        Mockito.verify(userRepository).getToken()
        Assert.assertNotNull(actualToken)
        Assert.assertEquals("S2xZnMbHw7RkGdYPirvWyN8Vf46TUpKs", actualToken)
    }
}