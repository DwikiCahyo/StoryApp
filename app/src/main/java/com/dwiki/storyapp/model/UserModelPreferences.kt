package com.dwiki.storyapp.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserModelPreferences(private val dataStore: DataStore<Preferences>) {

    fun getUser(): Flow<UserModel> {
        return dataStore.data.map {
            UserModel(
                it[NAME] ?: "",
                it[EMAIL] ?: "",
                it[PASSWORD] ?: "",
                it[USERID] ?: "",
                it[TOKEN] ?: "",
                it[STATUS] ?: false
            )
        }
    }


    suspend fun saveUser(user: UserModel) {
        dataStore.edit {
            it[NAME] = user.name
            it[EMAIL] = user.email
            it[PASSWORD] = user.password
            it[USERID] = user.userId
            it[TOKEN] = user.token
            it[STATUS] = user.isLogin
        }
    }

    suspend fun logOut() {
        dataStore.edit {
            it.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserModelPreferences? = null

        private val NAME = stringPreferencesKey("name")
        private val EMAIL = stringPreferencesKey("email")
        private val PASSWORD = stringPreferencesKey("password")
        private val USERID = stringPreferencesKey("userId")
        private val TOKEN = stringPreferencesKey("token")
        private val STATUS = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): UserModelPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserModelPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}