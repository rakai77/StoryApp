package com.example.storyapp.ui.story

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.storyapp.R
import com.example.storyapp.data.Resource
import com.example.storyapp.databinding.ActivityStoryBinding
import com.example.storyapp.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@AndroidEntryPoint
class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private lateinit var currentPhotoPath: String
    private lateinit var token: String

    private val viewModel by viewModels<StoryViewModel>()

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_bottom
        )
    }

    private var clicked = false

    private var file: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSION
            )
        }

        playAnimation()
        getToken()

        binding.fab.setOnClickListener { onAddButtonClicked() }
        binding.fabCamera.setOnClickListener { startTakePhoto() }
        binding.fabGallery.setOnClickListener { startGallery() }
        binding.btnAddStory.setOnClickListener { initObserver() }

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.fab.startAnimation(rotateOpen)
            binding.fabCamera.startAnimation(fromBottom)
            binding.fabGallery.startAnimation(fromBottom)
        } else {
            binding.fab.startAnimation(rotateClose)
            binding.fabCamera.startAnimation(toBottom)
            binding.fabGallery.startAnimation(toBottom)
        }
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.fabCamera.visibility = View.VISIBLE
            binding.fabGallery.visibility = View.VISIBLE
        } else {
            binding.fabCamera.visibility = View.INVISIBLE
            binding.fabGallery.visibility = View.INVISIBLE
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionGranted()) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.permission_failed),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getToken() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.token.collect {
                    token = it
                    Log.d("Check token", token)
                }
            }
        }
    }

    private fun initObserver() {
        if (file != null) {
            val description = binding.edtDescription.text.toString().trim()
            if (description.isEmpty()) {
                binding.edtDescription.error =
                    resources.getString(R.string.message_action, "description")
            } else {
                binding.progressbar.visibility = View.VISIBLE
                val media = Utils.reduceFileImage(file as File)
                val descMedia = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = media.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    media.name,
                    requestImageFile
                )
                lifecycleScope.launch {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.addStory("Bearer $token", imageMultipart, descMedia)
                        Log.d("check token view model",  token)
                        viewModel.addStory.observe(this@StoryActivity) { resource ->
                            if (resource != null) {
                                when (resource) {
                                    is Resource.Loading -> {
                                        binding.progressbar.visibility = View.VISIBLE
                                    }
                                    is Resource.Success -> {
                                        binding.progressbar.visibility = View.GONE
                                        Toast.makeText(this@StoryActivity, resource.data.message, Toast.LENGTH_SHORT)
                                            .show()
                                        finish()
                                    }
                                    is Resource.Error -> {
                                        binding.progressbar.visibility = View.GONE
                                        Toast.makeText(
                                            this@StoryActivity,
                                            "Failure : " + resource.error,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Toast.makeText(
                this,
                resources.getString(R.string.message_upload_image),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, resources.getString(R.string.open_gallery))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = Utils.uriToFile(selectedImg, this@StoryActivity)
            file = myFile
            binding.imgStory.setImageURI(selectedImg)
        }
    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        Utils.createTempFile(application).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                this@StoryActivity,
                "com.example.storyapp.ui.story",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            file = myFile
            val result = BitmapFactory.decodeFile(myFile.path)
            binding.imgStory.setImageBitmap(result)
        }
    }

    @SuppressLint("Recycle")
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgStory, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 3500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tflDescription =
            ObjectAnimator.ofFloat(binding.tflDescription, View.ALPHA, 1f).setDuration(300)
        val edtDescription =
            ObjectAnimator.ofFloat(binding.edtDescription, View.ALPHA, 1f).setDuration(300)
        val btnAddStory = ObjectAnimator.ofFloat(binding.btnAddStory, View.ALPHA, 1f).setDuration(300)
        val btnFabAdd = ObjectAnimator.ofFloat(binding.fab, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(
                tflDescription,
                edtDescription,
                btnAddStory,
                btnFabAdd
            )
            start()
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
        const val CAMERA_X_RESULT = 200
    }
}