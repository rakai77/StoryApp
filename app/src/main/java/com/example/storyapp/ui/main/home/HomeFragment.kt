package com.example.storyapp.ui.main.home

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.adapter.ListStoryAdapter
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.databinding.FragmentHomeBinding
import com.example.storyapp.ui.detail.DetailActivity
import com.example.storyapp.ui.detail.DetailActivity.Companion.STORY_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getToken()
        setupRecyclerView()

    }

    private fun getToken() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.token.collect {
                    token = it
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            val adapter = ListStoryAdapter(
                onClicked = {
                    val intent = Intent(requireContext(), DetailActivity::class.java)
                    intent.putExtra(STORY_ID, it)
                    startActivity(intent)
                }
            )
            rvStory.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )
            if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                rvStory.layoutManager = GridLayoutManager(requireContext(), 3)
            } else {
                binding.rvStory.layoutManager = LinearLayoutManager(requireContext())
            }
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.getALlStory("Bearer $token").collectLatest { adapter.submitData(viewLifecycleOwner.lifecycle, it) }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}