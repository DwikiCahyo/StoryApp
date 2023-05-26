package com.dwiki.storyapp.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dwiki.storyapp.DataDummy
import com.dwiki.storyapp.ResultStory
import com.dwiki.storyapp.api.response.ResponseRegister
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
class RegisterViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var registerViewModel: RegisterViewModel
    @Mock private lateinit var repositoryStory: RepositoryStory
    private val dummyResult = DataDummy.dummyRegisterResponseSuccess()


    private val dummyName = "dwiki"
    private val dummyEmail = "dwiki@gmail.com"
    private val dummyPass = "password"

    @Before
    fun setup() {
        registerViewModel = RegisterViewModel(repositoryStory)
    }

    @Test
    fun `when register success and not null will return error false and user created`(){
        val expectedResponse = MutableLiveData<ResultStory<ResponseRegister>>()
        expectedResponse.value = ResultStory.Success(dummyResult)

        `when` (repositoryStory.register(dummyName,dummyEmail,dummyPass)).thenReturn(expectedResponse)
        val actualResponse = registerViewModel.register(dummyName,dummyEmail,dummyPass).getOrAwaitValue()

        Mockito.verify(repositoryStory).register(dummyName,dummyEmail,dummyPass)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResultStory.Success)
        assertEquals(dummyResult, (actualResponse as ResultStory.Success).data)

    }

    @Test
    fun `when register bad request or error  will return error`(){
        val expectedResponse = MutableLiveData<ResultStory<ResponseRegister>>()
        expectedResponse.value = ResultStory.Error("error")

        `when` (repositoryStory.register(dummyName,dummyEmail,dummyPass)).thenReturn(expectedResponse)
        val actualResponse = registerViewModel.register(dummyName,dummyEmail,dummyPass).getOrAwaitValue()

        Mockito.verify(repositoryStory).register(dummyName,dummyEmail,dummyPass)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResultStory.Error)
        assertEquals("error",(actualResponse as ResultStory.Error).message)
    }
}