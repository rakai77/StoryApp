package com.example.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.Resource
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.ui.signup.SignupActivity
import com.example.storyapp.ui.viewmodel.UserViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupAction()
        setupViewModel()
        playAnimation()

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgLoginLogo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 3500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tflEmail = ObjectAnimator.ofFloat(binding.tflEmail, View.ALPHA, 1f).setDuration(300)
        val tflPassword =
            ObjectAnimator.ofFloat(binding.tflPassword, View.ALPHA, 1f).setDuration(300)
        val edtEmail = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(300)
        val edtPassword =
            ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(300)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(300)
        val tvOfferLogin =
            ObjectAnimator.ofFloat(binding.tvOfferSignup, View.ALPHA, 1f).setDuration(300)
        val btnOfferSignup =
            ObjectAnimator.ofFloat(binding.btnOfferSignup, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(
                tflEmail,
                tflPassword,
                edtEmail,
                edtPassword,
                btnLogin,
                tvOfferLogin,
                btnOfferSignup
            )
            start()
        }
    }


    private fun setupViewModel() {
        val viewModelFactory: UserViewModelFactory = UserViewModelFactory.getInstance(this)
        loginViewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        loginViewModel.getToken().observe(this) { token ->
            if (token.isNotEmpty()) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            login()
        }
        binding.btnOfferSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

    }

    private fun login() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        when {
            email.isEmpty() -> {
                binding.edtEmail.error = resources.getString(R.string.message_action, "email")
            }
            password.isEmpty() -> {
                binding.edtPassword.error = resources.getString(R.string.message_action, "password")
            }
            else -> {
                loginViewModel.login(email, password).observe(this) { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressbar.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.progressbar.visibility = View.GONE
                            val user = resource.data
                            if (user.error) {
                                Toast.makeText(this@LoginActivity, user.message, Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                val token = user.loginResult?.token ?: ""
                                loginViewModel.setToken(token, true)
                            }
                        }
                        is Resource.Error -> {
                            binding.progressbar.visibility = View.GONE
                            Toast.makeText(
                                this,
                                resources.getString(R.string.error_login),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

}