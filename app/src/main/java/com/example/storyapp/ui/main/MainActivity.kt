package com.example.storyapp.ui.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.ListStoryAdapter
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.data.Resource
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.detail.DetailActivity
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.ui.maps.MapsActivity
import com.example.storyapp.ui.story.StoryActivity
import com.example.storyapp.ui.viewmodel.StoryViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.rvStory.layoutManager = GridLayoutManager(this, 3)
        } else {
            binding.rvStory.layoutManager = LinearLayoutManager(this)
        }

        title = resources.getString(R.string.app_name)
        setupViewModel()

    }

    @Suppress("UNCHECKED_CAST")
    private fun setupViewModel() {
        val storyViewModelFactory: StoryViewModelFactory = StoryViewModelFactory.getInstance(this)
        mainViewModel = ViewModelProvider(this, storyViewModelFactory)[MainViewModel::class.java]

        mainViewModel.isLogin().observe(this) {
            if (!it) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        mainViewModel.getToken().observe(this) { token ->
            this.token = token
            if (token.isNotEmpty()) {
               val adapter = ListStoryAdapter()
                binding.rvStory.adapter = adapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        adapter.retry()
                    }
                )
                mainViewModel.getStories(token).observe(this) { result ->
                    adapter.submitData(lifecycle, result)
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_story -> {
                startActivity(Intent(this, StoryActivity::class.java))
                true
            }
            R.id.action_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.action_logout -> {
                mainViewModel.logout()
                true
            }
            else -> true
        }
    }

}