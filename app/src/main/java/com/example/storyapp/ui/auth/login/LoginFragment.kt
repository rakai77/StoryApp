package com.example.storyapp.ui.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.data.Resource
import com.example.storyapp.databinding.FragmentLoginBinding
import com.example.storyapp.ui.auth.AuthViewModel
import com.example.storyapp.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playAnimation()
        initObserver()
        setupAction()

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

    private fun setupForm() : Boolean{
        var form = false

        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        when {
            email.isEmpty() -> {
                binding.tflEmail.error = "Please fill your email address."
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tflEmail.error = "Please enter a valid email address."
            }
            password.isEmpty() -> {
                binding.tflPassword.error = "Please enter your password."
            }
            password.length < 6 -> {
                binding.tflPassword.error = "Password length must be 6 character."
            }
            else -> form = true
        }
        return form
    }

    private fun initObserver() {
        viewModel.loginState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Login ${result.data.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }
                is Resource.Error -> {
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Email or password not match",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            btnLogin.setOnClickListener {
                if (setupForm()) {
                    viewModel.login(
                        email = edtEmail.text.toString(),
                        password = edtPassword.text.toString(),
                    )
                }
            }
            btnOfferSignup.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}