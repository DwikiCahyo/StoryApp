package com.dwiki.storyapp.ViewModel


import androidx.lifecycle.*
import com.dwiki.storyapp.repository.RepositoryStory


class LoginViewModel(private val repository:RepositoryStory):ViewModel() {
    fun loginStory(email:String,password: String) = repository.login(email,password)
}