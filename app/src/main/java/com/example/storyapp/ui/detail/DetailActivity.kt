package com.example.storyapp.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ActivityDetailBinding
import com.example.storyapp.utils.Utils.setLocalDateFormat

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Detail Story"

        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_DATA)
        binding.apply {
            tvDetailUsername.text = story?.name
            tvDate.setLocalDateFormat(story?.createdAt.toString())
            tvDetailDescription.text = story?.description
        }
        Glide.with(this).load(story?.photoUrl).into(binding.imgDetailStory)
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}