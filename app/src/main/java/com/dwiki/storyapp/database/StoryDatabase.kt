package com.dwiki.storyapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dwiki.storyapp.api.response.ListStoryItem

@Database(
    entities = [ListStoryItem::class,RemoteKeys::class],
    version = 1,
    exportSchema = false
)

abstract class StoryDatabase:RoomDatabase() {
    abstract fun daoStory():StoryDao
    abstract fun daoRemoteKeys():RemoteKeysDao

    companion object {
       @Volatile
       private var INSTANCE:StoryDatabase? = null

       @JvmStatic
       fun getInstanceDatabase(context: Context):StoryDatabase{
           return INSTANCE ?: synchronized(this){
               INSTANCE ?: Room.databaseBuilder(
                   context.applicationContext,
                   StoryDatabase::class.java,"story_database"
               )
                   .fallbackToDestructiveMigration()
                   .build()
                   .also { INSTANCE = it }
           }
       }
    }
}