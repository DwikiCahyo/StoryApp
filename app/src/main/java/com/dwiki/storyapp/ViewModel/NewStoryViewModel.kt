package com.dwiki.storyapp.ViewModel


import androidx.lifecycle.ViewModel

import com.dwiki.storyapp.repository.RepositoryStory
import okhttp3.MultipartBody
import okhttp3.RequestBody


class NewStoryViewModel(private val repository: RepositoryStory):ViewModel() {
    fun postNewStory(
        token:String,
        desc:RequestBody,
        image:MultipartBody.Part,
        lat:RequestBody? = null,
        lon:RequestBody? =null
    ) = repository.newStory(token,desc,image,lat,lon)

}