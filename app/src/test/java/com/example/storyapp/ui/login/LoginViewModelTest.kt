package com.example.storyapp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.MainDispatcherRule
import com.example.storyapp.data.Resource
import com.example.storyapp.data.remote.UserRepository
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.dummy.DataDummy
import com.example.storyapp.getOrAwaitValue
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
class LoginViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var loginViewModel: LoginViewModel
    @Mock private lateinit var userRepository: UserRepository
    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()

    @Before
    fun setup() {
        loginViewModel = LoginViewModel(userRepository)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when login is called and return success`() {
        val expectedLogin = MutableLiveData<Resource<LoginResponse>>()
        expectedLogin.value = Resource.Success(dummyLoginResponse)

        Mockito.`when`(userRepository.login("dummy@mail.com", "password")).thenReturn(expectedLogin)

        val actualLoginResponse = loginViewModel.login("dummy@mail.com", "password").getOrAwaitValue()
        Mockito.verify(userRepository).login("dummy@mail.com", "password")
        Assert.assertNotNull(actualLoginResponse)
        Assert.assertTrue(actualLoginResponse is Resource.Success)
        Assert.assertSame(dummyLoginResponse, (actualLoginResponse as Resource.Success).data)
    }

    @Test
    fun `when login is failed and return error`() {
        val expectedLogin = MutableLiveData<Resource<LoginResponse>>()
        expectedLogin.value = Resource.Error("Error")

        Mockito.`when`(userRepository.login("dummy@mail.com", "password")).thenReturn(expectedLogin)

        val actualLoginResponse = loginViewModel.login("dummy@mail.com", "password").getOrAwaitValue()
        Mockito.verify(userRepository).login("dummy@mail.com", "password")
        Assert.assertNotNull(actualLoginResponse)
        Assert.assertTrue(actualLoginResponse is Resource.Error)
    }

    @Test
    fun `set token`() = runTest {
        loginViewModel.setToken("S2xZnMbHw7RkGdYPirvWyN8Vf46TUpKs", true)
        Mockito.verify(userRepository).setToken("S2xZnMbHw7RkGdYPirvWyN8Vf46TUpKs", true)
    }

    @Test
    fun `get token successfully`() {
        val expectedToken = flowOf("S2xZnMbHw7RkGdYPirvWyN8Vf46TUpKs")
        Mockito.`when`(userRepository.getToken()).thenReturn(expectedToken)

        val actualToken = loginViewModel.getToken().getOrAwaitValue()
        Mockito.verify(userRepository).getToken()
        Assert.assertNotNull(actualToken)
        Assert.assertEquals("S2xZnMbHw7RkGdYPirvWyN8Vf46TUpKs", actualToken)
    }

}