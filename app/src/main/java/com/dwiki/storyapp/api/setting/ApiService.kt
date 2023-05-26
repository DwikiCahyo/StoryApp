package com.dwiki.storyapp.api.setting

import com.dwiki.storyapp.api.response.ResponseListStory
import com.dwiki.storyapp.api.response.ResponseLogin
import com.dwiki.storyapp.api.response.ResponseNewStory
import com.dwiki.storyapp.api.response.ResponseRegister
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    //Login Form
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email:String,
        @Field("password") password:String
    ):ResponseLogin

    //Register Form
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name:String,
        @Field("email") email:String,
        @Field("password") password: String
    ):ResponseRegister

    //Get All Story
    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") token:String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ):ResponseListStory

    //Add Story
    @Multipart
    @POST("stories")
    suspend fun newStories(
        @Header("Authorization") token:String,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ):ResponseNewStory

    //Maps
    @GET("stories?location=1")
    suspend  fun getStoryLocation(
        @Header("Authorization") token:String,
    ):ResponseListStory

}