package com.example.storyapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.repository.auth.AuthRepository
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.Resource
import com.example.storyapp.data.remote.response.RegisterResponse
import com.example.storyapp.utils.Preference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val pref: Preference
) : ViewModel() {

    private val _loginState = MutableLiveData<Resource<LoginResponse>>()
    val loginState: LiveData<Resource<LoginResponse>> = _loginState

    private val _registerState = MutableLiveData<Resource<RegisterResponse>>()
    val registerState: LiveData<Resource<RegisterResponse>> = _registerState

    val getToken = pref.getToken()
    val getUsername = pref.getUsername()

    fun login(email: String, password: String) {
        repo.login(email, password).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _loginState.value = Resource.Loading
                }
                is Resource.Success -> {
                    result.data.let {
                        _loginState.value = Resource.Success(it)
                        pref.saveToken(it.loginResult?.token.toString())
                        pref.saveUsername(it.loginResult?.name.toString())
                    }
                }
                is Resource.Error -> {
                    _loginState.value = Resource.Error(result.error)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun register(name: String, email: String, password: String) {
        repo.register(name, email, password).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _registerState.value = Resource.Loading
                }
                is Resource.Success -> {
                    result.data.let {
                        _registerState.value = Resource.Success(it)
                    }
                }
                is Resource.Error -> {
                    _registerState.value = Resource.Error(result.error)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}