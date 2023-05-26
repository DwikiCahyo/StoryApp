package com.dwiki.storyapp.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dwiki.storyapp.DataDummy
import com.dwiki.storyapp.ResultStory
import com.dwiki.storyapp.api.response.LoginResult
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
class LoginViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var loginViewModel: LoginViewModel
    @Mock private lateinit var repositoryStory: RepositoryStory
    private val dummyResult = DataDummy.dummyLoginResponse().loginResult

    private val dummyEmail = "dwiki@gmail.com"
    private val dummyPass = "password"


    @Before
    fun setup() {
        loginViewModel = LoginViewModel(repositoryStory)
    }

    @Test
    fun `when login success and not null will return success`(){
        val expectedResponse = MutableLiveData<ResultStory<LoginResult>>()
        expectedResponse.value = ResultStory.Success(dummyResult)

        `when`(repositoryStory.login(dummyEmail, dummyPass)).thenReturn(expectedResponse)
        val actualResponse = loginViewModel.loginStory(dummyEmail, dummyPass).getOrAwaitValue()

        Mockito.verify(repositoryStory).login(dummyEmail, dummyPass)

        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResultStory.Success)
        assertEquals(dummyResult, (actualResponse as ResultStory.Success).data)


    }

    @Test
    fun `when login bad request or error  will return error`(){
        val expectedResponse = MutableLiveData<ResultStory<LoginResult>>()
        expectedResponse.value = ResultStory.Error("error")

        `when`(repositoryStory.login(dummyEmail, dummyPass)).thenReturn(expectedResponse)
        val actualResponse = loginViewModel.loginStory(dummyEmail, dummyPass).getOrAwaitValue()

        Mockito.verify(repositoryStory).login(dummyEmail,dummyPass)

        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResultStory.Error)
        assertEquals("error",(actualResponse as ResultStory.Error).message)



    }
}