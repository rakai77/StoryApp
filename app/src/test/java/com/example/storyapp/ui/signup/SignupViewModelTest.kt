package com.example.storyapp.ui.signup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.MainDispatcherRule
import com.example.storyapp.data.Resource
import com.example.storyapp.data.remote.UserRepository
import com.example.storyapp.data.remote.response.RegisterResponse
import com.example.storyapp.dummy.DataDummy
import com.example.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class SignupViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var signupViewModel: SignupViewModel
    @Mock private lateinit var userRepository: UserRepository
    private val dummySignupResponse = DataDummy.generateDummySignupResponse()

    @Before
    fun setup() {
        signupViewModel = SignupViewModel(userRepository)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @Test
    fun `when signup is called and return success`() {
        val expectedSignup = MutableLiveData<Resource<RegisterResponse>>()
        expectedSignup.value = Resource.Success(dummySignupResponse)

        Mockito.`when`(userRepository.register("dummy","dummy@mail.com", "password")).thenReturn(expectedSignup)

        val actualSignupResponse = signupViewModel.signup("dummy","dummy@mail.com", "password").getOrAwaitValue()
        Mockito.verify(userRepository).register("dummy","dummy@mail.com", "password")
        Assert.assertNotNull(actualSignupResponse)
        Assert.assertTrue(actualSignupResponse is Resource.Success)
        Assert.assertSame(dummySignupResponse, (actualSignupResponse as Resource.Success).data)
    }

    @Test
    fun `when signup is failed and return error`() {
        val expectedSignup = MutableLiveData<Resource<RegisterResponse>>()
        expectedSignup.value = Resource.Error("Error")

        Mockito.`when`(userRepository.register("dummy","dummy@mail.com", "password")).thenReturn(expectedSignup)

        val actualSignupResponse = signupViewModel.signup("dummy","dummy@mail.com", "password").getOrAwaitValue()
        Mockito.verify(userRepository).register("dummy","dummy@mail.com", "password")
        Assert.assertNotNull(actualSignupResponse)
        Assert.assertTrue(actualSignupResponse is Resource.Error)
    }
}