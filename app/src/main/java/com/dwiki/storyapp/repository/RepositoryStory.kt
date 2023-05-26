package com.dwiki.storyapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.dwiki.storyapp.ResultStory
import com.dwiki.storyapp.api.response.ListStoryItem
import com.dwiki.storyapp.api.response.LoginResult
import com.dwiki.storyapp.api.response.ResponseNewStory
import com.dwiki.storyapp.api.response.ResponseRegister
import com.dwiki.storyapp.api.setting.ApiService
import com.dwiki.storyapp.data.StoryRemoteMediator
import com.dwiki.storyapp.database.StoryDatabase
import com.dwiki.storyapp.model.UserModel
import com.dwiki.storyapp.model.UserModelPreferences
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RepositoryStory(
    private val storyDatabase: StoryDatabase,
    private val ApiService: ApiService,
    private val pref: UserModelPreferences

) {

    //get user
    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    //Login
    fun login (email:String,password:String):LiveData<ResultStory<LoginResult>> = liveData {
        emit(ResultStory.Loading)
        try {
            val response = ApiService.login(email,password)
            if (!response.error){
                emit(ResultStory.Success(response.loginResult))
            } else {
                emit(ResultStory.Error(response.message))
                Log.e(TAG, response.message)
            }
        }catch (e:Exception){
            emit(ResultStory.Error(e.message.toString()))
            Log.e(TAG, e.message.toString())
        }
    }

    //Register
    fun register(name:String,email: String,password: String):LiveData<ResultStory<ResponseRegister>> = liveData {
        emit(ResultStory.Loading)
        try {
            val response = ApiService.register(name,email,password)
            if (!response.error){
                emit(ResultStory.Success(response))
            } else {
                emit(ResultStory.Error(response.message))
                Log.e(TAG, response.message)
            }
        } catch (e:Exception){
            emit(ResultStory.Error(e.message.toString()))
            Log.e(TAG, e.message.toString())
        }
    }

    //get story
    fun getAllStoryMap(token:String):LiveData<ResultStory<List<ListStoryItem>>> = liveData {
        emit(ResultStory.Loading)
        try {
            val response = ApiService.getStoryLocation("Bearer $token")
            if (!response.error){
                emit(ResultStory.Success(response.listStory))
            }
        } catch (E:Exception) {
            emit(ResultStory.Error(E.message.toString()))
            Log.e(TAG,"Get Story Map Exception: ${E.message.toString()}")
        }
    }
    //new story
    fun newStory(
            token: String,
            desc: RequestBody,
            image:MultipartBody.Part,
            lat:RequestBody? = null,
            lon:RequestBody? = null
    ):LiveData<ResultStory<ResponseNewStory>> = liveData {
        emit(ResultStory.Loading)
        try {
            val response = ApiService.newStories("Bearer $token",desc,image,lat,lon)
            if (!response.error){
                emit(ResultStory.Success(response))
            } else{
                Log.e(TAG,"Failed post story: ${response.message}")
                emit(ResultStory.Error(response.message))
            }
        } catch (E:Exception) {
            emit(ResultStory.Error(E.message.toString()))
            Log.e(TAG,"Get Story Map Exception: ${E.message.toString()}")
        }
    }

    //setup pagging
    fun getStoryPaging(token: String):LiveData<PagingData<ListStoryItem>>{
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator( storyDatabase, ApiService, token),
            pagingSourceFactory = {
               storyDatabase.daoStory().getStory()
            }
        ).liveData

    }

    companion object {
        private const val TAG = "Repository"
    }


}