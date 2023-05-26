package com.dwiki.storyapp.ViewModel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dwiki.storyapp.DataDummy
import com.dwiki.storyapp.getOrAwaitValue
import com.dwiki.storyapp.model.UserModel
import com.dwiki.storyapp.model.UserModelPreferences
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
class AuthViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var authViewModel: AuthViewModel
    @Mock private lateinit var repositoryStory: RepositoryStory
    private val dummyResult = DataDummy.dummyUserModel()

    @Before
    fun setup() {
        authViewModel = AuthViewModel(repositoryStory)
    }

    @Test
    fun `when getUser() called will return user model `(){
        val expectedResponse = MutableLiveData(DataDummy.dummyUserModel())

        `when`(repositoryStory.getUser()).thenReturn(expectedResponse)
        val actualResponse = authViewModel.getUser().getOrAwaitValue()

        Mockito.verify(repositoryStory).getUser()


        assertTrue(actualResponse is UserModel) // mengecek return dari get User apakah menghasilkan user model
        assertEquals(dummyResult,actualResponse)
        assertEquals(dummyResult.token, actualResponse.token)


    }
}

