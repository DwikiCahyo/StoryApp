package com.dwiki.storyapp.ViewModel


import androidx.lifecycle.ViewModel
import com.dwiki.storyapp.repository.RepositoryStory

class RegisterViewModel(private val repository: RepositoryStory):ViewModel() {
    fun register(name:String,email:String,password:String) = repository.register(name, email, password)
}