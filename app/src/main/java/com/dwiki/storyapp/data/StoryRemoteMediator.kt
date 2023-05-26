package com.dwiki.storyapp.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dwiki.storyapp.api.response.ListStoryItem
import com.dwiki.storyapp.api.setting.ApiService
import com.dwiki.storyapp.database.RemoteKeys
import com.dwiki.storyapp.database.StoryDatabase


@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val dbStory: StoryDatabase,
    private val apiService: ApiService,
    private val token: String
):RemoteMediator<Int,ListStoryItem>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryItem>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH ->{
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
    }
        return try {
            val dataResponse = apiService.getStory("Bearer $token",page,state.config.pageSize).listStory
            val endOfPaginationReached = dataResponse.isEmpty()

            dbStory.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    dbStory.daoRemoteKeys().deleteRemoteKeys()
                    dbStory.daoStory().deleteAll()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = dataResponse.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                if (keys == null) {
                    Log.e("mediator","Data tidak masuk")
                } else {
                    Log.e("mediator","succes")
                }


                dbStory.daoRemoteKeys().insertAll(keys)
                dbStory.daoStory().insertStory(dataResponse)
            }
            Log.e("mediator","$dataResponse")
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e:Exception){
            Log.e("Tag", e.toString())
             MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            dbStory.daoRemoteKeys().getRemoteKeysId(it.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
            dbStory.daoRemoteKeys().getRemoteKeysId(it.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let {
                dbStory.daoRemoteKeys().getRemoteKeysId(it)
            }
        }
    }
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}