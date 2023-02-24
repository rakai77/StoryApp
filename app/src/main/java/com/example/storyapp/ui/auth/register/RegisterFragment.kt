package com.example.storyapp.ui.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.data.Resource
import com.example.storyapp.databinding.FragmentRegisterBinding
import com.example.storyapp.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playAnimation()
        initObserver()
        setupAction()
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

    private fun setupForm() : Boolean{
        var form = false

        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        when {
            name.isEmpty() -> {
                binding.edtName.error = "Please fill your name."
            }
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
        viewModel.registerState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Register ${result.data.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
                is Resource.Error -> {
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Email already to used",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            btnSignup.setOnClickListener {
                if (setupForm()) {
                    viewModel.register(
                        name = edtName.text.toString(),
                        email = edtEmail.text.toString(),
                        password = edtPassword.text.toString(),
                    )
                }
            }
            btnOfferLogin.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}