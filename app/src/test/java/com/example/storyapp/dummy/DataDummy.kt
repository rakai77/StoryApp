package com.example.storyapp.dummy

import com.example.storyapp.data.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object DataDummy {

    fun generateDummyLoginResponse(): LoginResponse {
        val loginResult = LoginResult(
            "anonymous",
            "12345",
            "token"
        )

        return LoginResponse(
            loginResult = loginResult,
            error = false,
            message = "success"
        )
    }

    fun generateDummySignupResponse(): RegisterResponse {
        return RegisterResponse(
            error = false,
            message = "success"
        )
    }

    fun generateDummyStoryResponse(): StoriesResponse {
        val data = arrayListOf<ListStoryItem>()

        for (i in 0 until 10) {
            val story = ListStoryItem(
                "story-FvU4u0Vp2S3PMsFg",
                "Dimas",
                "2022-01-08T06:34:18.598Z",
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                "Lorem Ipsum",
                -10.212,
                -16.002
            )

            data.add(story)
        }
        return StoriesResponse(data, false, "Stories fetched successfully")
    }

    fun generateDummyListStory(): List<ListStoryItem> {
        val data = arrayListOf<ListStoryItem>()

        for (i in 0 until 10) {
            val story = ListStoryItem(
                "story-FvU4u0Vp2S3PMsFg",
                "Dimas",
                "2022-01-08T06:34:18.598Z",
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                "Lorem Ipsum",
                -10.212,
                -16.002
            )

            data.add(story)
        }
        return data
    }

    fun generateDummyAddNewStoryResponse(): AddNewStoryResponse {
        return AddNewStoryResponse(
            error = false,
            message = "success"
        )
    }

    fun generateDummyMultipartFile(): MultipartBody.Part {
        val dummyText = "text"
        return MultipartBody.Part.create(dummyText.toRequestBody())
    }

    fun generateDummyRequestBody(): RequestBody {
        val dummyText = "text"
        return dummyText.toRequestBody()
    }

}