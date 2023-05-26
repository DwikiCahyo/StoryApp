package com.dwiki.storyapp.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.dwiki.storyapp.MainActivity
import com.dwiki.storyapp.R
import com.dwiki.storyapp.ResultStory
import com.dwiki.storyapp.ViewModel.LoginViewModel
import com.dwiki.storyapp.databinding.ActivityLoginBinding
import com.dwiki.storyapp.model.UserModel
import com.dwiki.storyapp.model.UserModelPreferences
import com.dwiki.storyapp.repository.ViewModelFactoryRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_pref")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactoryRepository.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        emailListener()
        passwordListener()
        buttonSignInListener()

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setSignInEnable() {
        val email = binding.edtEmail.text
        val pass = binding.edtPass.text

        binding.btnSignIn.isEnabled = pass != null && email != null && binding.edtPass.text.toString().length >= 6 && validEmail(binding.edtEmail.text.toString())
    }

    private fun passwordListener() {
        binding.edtPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //nothing
                setSignInEnable()
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) binding.errPass.visibility =
                    View.VISIBLE else binding.errPass.visibility = View.GONE
            }

        })
    }

    private fun emailListener() {
        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //nothing
                setSignInEnable()
            }

            override fun afterTextChanged(s: Editable) {
                if (!validEmail(s) && s.isNotEmpty()) binding.errEmail.visibility =
                    View.VISIBLE else binding.errEmail.visibility = View.GONE
            }

        })
    }

    private fun buttonSignInListener() {
        binding.btnSignIn.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val pass = binding.edtPass.text.toString()

            loginViewModel.loginStory(email,pass).observe(this){
                val pref = UserModelPreferences.getInstance(dataStore)
                when(it){
                    is ResultStory.Loading -> {
                        showLoading(true)
                    } is ResultStory.Success -> {
                        binding.progressBar.visibility =View.GONE
                        val userModel = UserModel(
                            it.data.name,
                            email,
                            pass,
                            it.data.userId,
                            it.data.token,
                            true )
                        saveUser(pref, userModel)
                        intentToMain()
                } is ResultStory.Error -> {
                        showLoading(false)
                        if (it.message == "HTTP 401 Unauthorized"){
                            AlertDialog.Builder(this).apply {
                                setMessage(getString(R.string.email_pass_wrong))
                                setPositiveButton(getString(R.string.close_login)){
                                        dialog,_ -> dialog.dismiss()
                                }
                                create()
                                show()
                                showLoading(false)
                            }

                        }  else {
                            AlertDialog.Builder(this).apply {
                                setMessage("Error : $it")
                                setPositiveButton(getString(R.string.close_login)){
                                        dialog,_ -> dialog.dismiss()
                                }
                                create()
                                show()
                                showLoading(false)
                            }
                        }

                    }
                }
            }
        }
    }

    private fun intentToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun saveUser(
        pref: UserModelPreferences,
        userModel: UserModel
    ) {
        lifecycleScope.launchWhenStarted {
            pref.saveUser(userModel)
        }
    }

    private fun validEmail(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    private fun showLoading(isLoading:Boolean) {
        binding.apply {
            if (isLoading) progressBar.visibility = View.VISIBLE
            else progressBar.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}


