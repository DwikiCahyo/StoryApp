package com.dwiki.storyapp

import com.dwiki.storyapp.api.response.*
import com.dwiki.storyapp.model.UserModel

object DataDummy {


    fun dummyLoginResponse(): ResponseLogin {
        return ResponseLogin(
            loginResult = dummyLoginResult(),
            error = false,
            message = "Success"
        )
    }

    private fun dummyLoginResult(): LoginResult {
        return LoginResult(
            name = "user",
            userId = "1234",
            token = "token"
        )
    }

    fun dummyRegisterResponseSuccess():ResponseRegister{
        return ResponseRegister(
            error = false,
            message = "User created"
        )
    }

    fun dummyMapResponse():List<ListStoryItem>{
        val listStory = ArrayList<ListStoryItem>()
        // add story
        for ( i in 0..5){
            val storyItem = ListStoryItem(
                photoUrl = "URL Story $i",
                createdAt = "2022-12-01T24:24:24.244Z",
                name = "name $i",
                description = "description $i",
                id = "$i",
                lat = 0.69,
                lon = 0.69
            )
            listStory.add(storyItem)
        }
        return listStory
    }

    fun dummyListStory():List<ListStoryItem>{
        val story = arrayListOf<ListStoryItem>()
        for (i in 0..5){
            val item = ListStoryItem(
                photoUrl = "URL Story $i",
                createdAt = "2022-12-01T24:24:24.244Z",
                name = "name $i",
                description = "description $i",
                id = "$i",
                lat = 0.69,
                lon = 0.69
            )
            story.add(item)
        }
        return story
    }

    fun dummyNewStoryResponse():ResponseNewStory{
        return ResponseNewStory(
            false,
            "Success"
        )
    }

    fun dummyUserModel():UserModel{
        return UserModel(
            name = "name",
            email = "email@gmail.com",
            password = "password",
            userId = "userId",
            token = "token",
            isLogin = true
        )
    }
}