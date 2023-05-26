package com.dwiki.storyapp.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dwiki.storyapp.api.response.ListStoryItem

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story:List<ListStoryItem>)

    @Query("SELECT * FROM story")
    fun getStory():PagingSource<Int,ListStoryItem>

    @Query("DELETE FROM story")
    suspend fun deleteAll()


}