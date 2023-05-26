package com.dwiki.storyapp.ViewModel


import androidx.lifecycle.ViewModel
import com.dwiki.storyapp.repository.RepositoryStory

class MapViewModel(private val repository: RepositoryStory):ViewModel() {

    fun getMapStories(token:String) =  repository.getAllStoryMap(token)

}