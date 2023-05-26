package com.dwiki.storyapp.ViewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dwiki.storyapp.api.response.ListStoryItem
import com.dwiki.storyapp.repository.RepositoryStory


class MainViewModel(private val repositoryStory: RepositoryStory):ViewModel() {

    fun getStory(token: String):LiveData<PagingData<ListStoryItem>>{
        return repositoryStory.getStoryPaging(token).cachedIn(viewModelScope)
    }

}