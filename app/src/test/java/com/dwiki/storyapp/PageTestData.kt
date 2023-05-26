package com.dwiki.storyapp

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dwiki.storyapp.api.response.ListStoryItem

class PageTestData:

    PagingSource<Int, LiveData<List<ListStoryItem>>>() {

        companion object {
            fun snapshot (items: List<ListStoryItem>): PagingData<ListStoryItem> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
            return 0
        }

        override suspend fun load(params: PagingSource.LoadParams<Int>): PagingSource.LoadResult<Int, LiveData<List<ListStoryItem>>> {
            return PagingSource.LoadResult.Page(emptyList(), 0, 1)
        }
}