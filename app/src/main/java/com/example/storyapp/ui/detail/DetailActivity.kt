package com.example.storyapp.ui.detail

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.storyapp.data.Resource
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.data.remote.response.DetailStoryResponse
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.databinding.ActivityDetailBinding
import com.example.storyapp.utils.Utils.setLocalDateFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var storyId: String = ""
    private var token: String = ""

    private val viewModel by viewModels<DetailViewModel>()

    private lateinit var storyItem: DetailStoryResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intentData = intent.getStringExtra(STORY_ID)
        if (intentData != null) {
            storyId = intentData
        }
        if (storyId.isEmpty()) {
            val data: Uri? = intent.data
            val id = data?.getQueryParameter("story_id")
            if (id != null) {
                storyId = id.toString()
            }
        }

        initObserver()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getToken.collect {
                        token = it
                        Log.d("Check token detail", token)
                    }
                }
                launch {
                    initViewModel()
                }
            }
        }
    }

    private fun initViewModel() {
        viewModel.getDetailStories("Bearer $token", storyId)
        viewModel.detailStory.observe(this@DetailActivity) { result ->
            when(result) {
                is Resource.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressbar.visibility = View.GONE
                    result.data.let { storyItem = it }
                    initData(result.data)

                }
                is Resource.Error -> {
                    binding.progressbar.visibility = View.GONE
                }
            }
        }
    }

    private fun initData(data: DetailStoryResponse) {
        binding.apply {
            customToolbar.title = data.story?.name
            toolbarTitle.text = data.story?.name
            tvDetailUsername.text = data.story?.name
            tvDate.setLocalDateFormat(data.story?.createdAt.toString())
            tvDetailDescription.text = data.story?.description
        }
        Glide.with(this).load(data.story?.photoUrl).into(binding.imgDetailStory)
    }

    companion object {
        const val STORY_ID = "story_id"
    }
}