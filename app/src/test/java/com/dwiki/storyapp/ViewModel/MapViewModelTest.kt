package com.dwiki.storyapp.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dwiki.storyapp.DataDummy
import com.dwiki.storyapp.ResultStory
import com.dwiki.storyapp.api.response.ListStoryItem
import com.dwiki.storyapp.getOrAwaitValue
import com.dwiki.storyapp.repository.RepositoryStory
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var mapViewModel: MapViewModel
    @Mock private lateinit var repositoryStory: RepositoryStory
    private val dummyResult = DataDummy.dummyMapResponse()

    private var token = "token"

    @Before
    fun setup() {
        mapViewModel = MapViewModel(repositoryStory)
    }

    @Test
    fun `when get map success and not null will return success `(){
        val expectedResponse = MutableLiveData<ResultStory<List<ListStoryItem>>>()
        expectedResponse.value = ResultStory.Success(dummyResult)

        `when`(repositoryStory.getAllStoryMap(token)).thenReturn(expectedResponse)
        val actualResponse = mapViewModel.getMapStories(token).getOrAwaitValue()


        Mockito.verify(repositoryStory).getAllStoryMap(token)

        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResultStory.Success)
        assertEquals(dummyResult,(actualResponse as ResultStory.Success).data)

    }


    @Test
    fun `when get map error will return error`(){
        val expectedResponse = MutableLiveData<ResultStory<List<ListStoryItem>>>()
        expectedResponse.value = ResultStory.Error("error")

        `when`(repositoryStory.getAllStoryMap(token)).thenReturn(expectedResponse)
        val actualResponse = mapViewModel.getMapStories(token).getOrAwaitValue()

        Mockito.verify(repositoryStory).getAllStoryMap(token)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResultStory.Error)
        assertEquals("error",(actualResponse as ResultStory.Error).message)
    }

}