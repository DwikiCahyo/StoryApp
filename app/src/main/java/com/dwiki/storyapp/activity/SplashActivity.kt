package com.dwiki.storyapp.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dwiki.storyapp.MainActivity
import com.dwiki.storyapp.R
import com.dwiki.storyapp.ViewModel.AuthViewModel
import com.dwiki.storyapp.repository.ViewModelFactoryRepository

class SplashActivity : AppCompatActivity() {

    private val authViewModel:AuthViewModel by viewModels {
        ViewModelFactoryRepository.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()


        Handler().postDelayed({
            setupViewModel()
            finish()
        }, 2000)

    }

    private fun setupViewModel() {
        authViewModel.getUser().observe(this){
            if (it.isLogin){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

    }

}