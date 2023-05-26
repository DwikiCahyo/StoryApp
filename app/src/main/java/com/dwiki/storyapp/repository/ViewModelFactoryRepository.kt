package com.dwiki.storyapp.repository

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dwiki.storyapp.DependecyInjection.Injection
import com.dwiki.storyapp.ViewModel.*

class ViewModelFactoryRepository private constructor(
    private val storyRepository: RepositoryStory
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java)-> {
                LoginViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java)->{
                RegisterViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java)->{
                AuthViewModel(storyRepository) as T
            } modelClass.isAssignableFrom(NewStoryViewModel::class.java) -> {
                NewStoryViewModel(storyRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactoryRepository? = null
        fun getInstance(context: Context): ViewModelFactoryRepository =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactoryRepository(Injection.provideStoryRepository(context))
            }.also { instance = it }
    }
}