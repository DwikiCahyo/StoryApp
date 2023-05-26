package com.dwiki.storyapp.ViewModel


import androidx.lifecycle.ViewModel
import com.dwiki.storyapp.repository.RepositoryStory

class AuthViewModel ( private val repositoryStory: RepositoryStory) : ViewModel(){
    fun getUser() = repositoryStory.getUser()
}