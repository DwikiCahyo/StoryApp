package com.dwiki.storyapp.DependecyInjection

import android.content.Context
import com.dwiki.storyapp.api.setting.ApiConfig
import com.dwiki.storyapp.dataStore
import com.dwiki.storyapp.database.StoryDatabase
import com.dwiki.storyapp.model.UserModelPreferences
import com.dwiki.storyapp.repository.RepositoryStory

object Injection {
    fun provideStoryRepository(context: Context): RepositoryStory {
        val database = StoryDatabase.getInstanceDatabase(context)
        val apiService = ApiConfig().getApiService()
        val preferences = UserModelPreferences.getInstance(context.dataStore)
        return RepositoryStory(database,apiService, preferences)
    }
}