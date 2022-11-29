package com.example.storyapp.ui.signup

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.remote.UserRepository

class SignupViewModel(private val repository: UserRepository) : ViewModel() {

    fun signup(name: String, email: String, password: String) =
        repository.register(name, email, password)
}