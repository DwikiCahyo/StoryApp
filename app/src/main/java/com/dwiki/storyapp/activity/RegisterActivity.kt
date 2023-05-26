package com.dwiki.storyapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.dwiki.storyapp.R
import com.dwiki.storyapp.ResultStory
import com.dwiki.storyapp.ViewModel.RegisterViewModel
import com.dwiki.storyapp.databinding.ActivityRegisterBinding
import com.dwiki.storyapp.repository.ViewModelFactoryRepository

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactoryRepository.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        registerEnable()
        emailListener()
        passwordListener()
        nameListener()
        setupRegister()
    }

    private fun nameListener() {
        binding.edtName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //nothing
                registerEnable()

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) binding.errPass.visibility =
                    View.VISIBLE else binding.errPass.visibility = View.GONE
            }

        })
    }

    private fun registerEnable() {
        val email = binding.edtEmail.text
        val name = binding.edtName.text
        val password = binding.edtPass1.text

        binding.btnRegister.isEnabled = password != null && email != null && name != null && binding.edtPass.text.toString().length >= 6 && validEmail(binding.edtEmail.text.toString())
    }


    private fun setupRegister() {
        binding.apply {
            btnRegister.setOnClickListener {
                val name = edtName.text.toString()
                val email = edtEmail.text.toString()
                val password = edtPass1.text.toString()
                val password2 = edtPass.text.toString()

                if (password == password2){
                    viewModel.register(name, email, password).observe(this@RegisterActivity){
                        when(it){
                            is ResultStory.Loading -> {
                                showLoading(true)
                            }
                            is ResultStory.Success -> {
                                showLoading(false)
                                AlertDialog.Builder(this@RegisterActivity).apply {
                                    setMessage(getString(R.string.success_added))
                                    setPositiveButton(getString(R.string.login)) { _, _ ->
                                        val intent = Intent(context, LoginActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                            }
                            is ResultStory.Error -> {
                                showLoading(false)
                                emailTaken(it.message)
                            }
                        }
                    }
                } else {
                    AlertDialog.Builder(this@RegisterActivity).apply {
                        setMessage(getString(R.string.pass_dont_match))
                        setPositiveButton(getString(R.string.back)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        create()
                        show()
                    }
                }
            }
        }
    }

    private fun emailTaken(message:String) {
            if (message == "HTTP 400 Bad Request") {
                AlertDialog.Builder(this@RegisterActivity).apply {
                    setMessage(getString(R.string.email_taken))
                    setPositiveButton(getString(R.string.back)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    create()
                    show()
                }
            }

    }

    private fun passwordListener() {
        binding.edtPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //nothing
                registerEnable()

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
                registerEnable()
            }

            override fun afterTextChanged(s: Editable) {
                if (!validEmail(s) && s.isNotEmpty()) binding.errEmail.visibility =
                    View.VISIBLE else binding.errEmail.visibility = View.GONE
            }

        })
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
}