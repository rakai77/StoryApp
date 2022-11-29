package com.example.storyapp.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.Resource
import com.example.storyapp.databinding.ActivitySignupBinding
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.ui.viewmodel.UserViewModelFactory

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupViewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
        setupAction()
        setupViewModel()

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgSignupLogo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 3500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tflName = ObjectAnimator.ofFloat(binding.tflName, View.ALPHA, 1f).setDuration(300)
        val edtName = ObjectAnimator.ofFloat(binding.edtName, View.ALPHA, 1f).setDuration(300)
        val tflEmail = ObjectAnimator.ofFloat(binding.tflEmail, View.ALPHA, 1f).setDuration(300)
        val tflPassword =
            ObjectAnimator.ofFloat(binding.tflPassword, View.ALPHA, 1f).setDuration(300)
        val edtEmail = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(300)
        val edtPassword =
            ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(300)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(
                tflName,
                edtName,
                tflEmail,
                tflPassword,
                edtEmail,
                edtPassword,
                btnLogin,
            )
            start()
        }
    }

    private fun setupAction() {
        binding.btnSignup.setOnClickListener {
            val name = binding.edtName.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            when {
                name.isEmpty() -> {
                    binding.edtName.error = resources.getString(R.string.message_action, "name")
                }
                email.isEmpty() -> {
                    binding.edtEmail.error = resources.getString(R.string.message_action, "email")
                }
                password.isEmpty() -> {
                    binding.edtPassword.error = resources.getString(R.string.message_action, "password")
                }
                else -> {
                    signupViewModel.signup(name, email, password).observe(this) { resource ->
                        if (resource != null) {
                            when (resource) {
                                is Resource.Loading -> {
                                    binding.progressbar.visibility = View.VISIBLE
                                }
                                is Resource.Success -> {
                                    binding.progressbar.visibility = View.GONE
                                    val user = resource.data
                                    if (user.error) {
                                        Toast.makeText(
                                            this@SignupActivity,
                                            user.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        AlertDialog.Builder(this@SignupActivity).apply {
                                            setTitle("message")
                                            setMessage("Your account created!")
                                            setPositiveButton("Ok") { _, _ ->
                                                finish()
                                            }
                                            create()
                                            show()
                                        }
                                    }
                                }
                                is Resource.Error -> {
                                    binding.progressbar.visibility = View.GONE
                                    Toast.makeText(
                                        this,
                                        resources.getString(R.string.error_signup),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }
        binding.btnOfferLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupViewModel() {
        val viewModelFactory: UserViewModelFactory = UserViewModelFactory.getInstance(this)
        signupViewModel = ViewModelProvider(this, viewModelFactory)[SignupViewModel::class.java]
    }

}