package com.dwiki.storyapp

sealed class ResultStory <out R> private constructor(){
    object Loading : ResultStory<Nothing>()
    data class Error(val message:String): ResultStory<Nothing>()
    data class Success<out T>(val data: T):ResultStory<T>()
}