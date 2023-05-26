package com.dwiki.storyapp.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.dwiki.storyapp.DataDummy
import com.dwiki.storyapp.MainCoroutineRule
import com.dwiki.storyapp.PageTestData
import com.dwiki.storyapp.adapter.ListStoryAdapter
import com.dwiki.storyapp.api.response.ListStoryItem
import com.dwiki.storyapp.getOrAwaitValue
import com.dwiki.storyapp.repository.RepositoryStory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    private lateinit var mainViewModel: MainViewModel
    @Mock private lateinit var repositoryStory: RepositoryStory
    private val dummyResult = DataDummy.dummyListStory()
    private val dummyToken = "token"

    @Before
    fun setup() {
        mainViewModel = MainViewModel(repositoryStory)
    }

    @Test
    fun `when getStoryPaging called will return list story  `() = runTest {
        val data = PageTestData.snapshot(dummyResult)
        val expectedResponse = MutableLiveData<PagingData<ListStoryItem>>()
        expectedResponse.value = data

        `when`(repositoryStory.getStoryPaging(dummyToken)).thenReturn(expectedResponse)
        val actualStory = mainViewModel.getStory(dummyToken).getOrAwaitValue()

        val differCall = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainCoroutineRules.dispatcher,
            workerDispatcher = mainCoroutineRules.dispatcher,
        )

        differCall.submitData(actualStory)

        Mockito.verify(repositoryStory).getStoryPaging(dummyToken)
        assertNotNull(differCall.snapshot())
        assertEquals(dummyResult, differCall.snapshot())
        assertEquals(dummyResult.size, differCall.snapshot().size)
        assertEquals(dummyResult[0].name, differCall.snapshot()[0]?.name)


    }

}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}