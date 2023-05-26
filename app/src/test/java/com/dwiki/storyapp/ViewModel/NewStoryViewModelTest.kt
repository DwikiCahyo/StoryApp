package com.dwiki.storyapp.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dwiki.storyapp.DataDummy
import com.dwiki.storyapp.ResultStory
import com.dwiki.storyapp.api.response.ResponseNewStory
import com.dwiki.storyapp.getOrAwaitValue
import com.dwiki.storyapp.repository.RepositoryStory
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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
class NewStoryViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var newStoryViewModel: NewStoryViewModel
    @Mock
    private lateinit var repositoryStory: RepositoryStory

    private val dummyResponse = DataDummy.dummyNewStoryResponse()
    private val dummyToken = "token"
    private val dummyImage = MultipartBody.Part.create("photo".toRequestBody())
    private val dummyDesc = "desc".toRequestBody()
    private val dummyLat= "0.69".toRequestBody()
    private val dummyLon = "0.69".toRequestBody()

    @Before
    fun setup() {
        newStoryViewModel = NewStoryViewModel(repositoryStory)
    }

    @Test
    fun `when add new story success and not null will return success`(){
        val expectedResponse = MutableLiveData<ResultStory<ResponseNewStory>>()
        expectedResponse.value = ResultStory.Success(dummyResponse)

        `when`(repositoryStory.newStory(
            dummyToken,
            dummyDesc,
            dummyImage,
            dummyLat,
            dummyLon
        )
        ).thenReturn( expectedResponse)

        val actualResponse = newStoryViewModel.postNewStory(
            dummyToken,
            dummyDesc,
            dummyImage,
            dummyLat,
            dummyLon
        ).getOrAwaitValue()

        Mockito.verify(repositoryStory).newStory(
            dummyToken,
            dummyDesc,
            dummyImage,
            dummyLat,
            dummyLon
        )

        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResultStory.Success)
        assertEquals(dummyResponse, (actualResponse as ResultStory.Success).data)


    }


}