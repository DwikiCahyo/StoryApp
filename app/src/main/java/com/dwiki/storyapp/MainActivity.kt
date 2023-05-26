package com.dwiki.storyapp


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dwiki.storyapp.ViewModel.AuthViewModel
import com.dwiki.storyapp.ViewModel.MainViewModel
import com.dwiki.storyapp.activity.AddNewStory
import com.dwiki.storyapp.activity.LoginActivity
import com.dwiki.storyapp.activity.MapsActivity
import com.dwiki.storyapp.activity.MapsActivity.Companion.EXTRA_USER
import com.dwiki.storyapp.adapter.ListStoryAdapter
import com.dwiki.storyapp.adapter.LoadingStateAdapter

import com.dwiki.storyapp.databinding.ActivityMainBinding
import com.dwiki.storyapp.model.UserModel
import com.dwiki.storyapp.model.UserModelPreferences
import com.dwiki.storyapp.repository.ViewModelFactoryRepository
import kotlinx.coroutines.launch


val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_pref")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var userModel:UserModel? = null
    private lateinit var adapter: ListStoryAdapter
    private val pref:UserModelPreferences = UserModelPreferences(dataStore)


   private val mainViewModel:MainViewModel by viewModels {
       ViewModelFactoryRepository.getInstance(this)
   }

    private val authViewModel:AuthViewModel by viewModels {
        ViewModelFactoryRepository.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        swipeRefresh()
        setupViewModel()
        recycleView()
        addNewStory()
    }

    private fun addNewStory() {
        binding.floatingButton.setOnClickListener {
            val intent = Intent(this,AddNewStory::class.java)
            intent.putExtra(USER,userModel)
            startActivity(intent)
        }
    }

    private fun recycleView() {
        adapter = ListStoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        binding.rvStory.setHasFixedSize(true)
        binding.rvStory.layoutManager = LinearLayoutManager(this)
        binding.rvStory.adapter = adapter
        Log.e("Tag","Adapter created")
    }

    private fun setupViewModel() {
        authViewModel.getUser().observe(this) { it ->
            userModel = UserModel(
                it.name,
                it.email,
                it.password,
                it.userId,
                it.token,
                true
            )
            supportActionBar?.title = getString(R.string.welcome, it.name)
            Log.e("Tag", it.name)

            mainViewModel.getStory(it.token).observe(this){
                adapter.submitData(lifecycle,it)
            }
        }
    }

    private fun swipeRefresh(){
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collect {
                binding.swipe.isRefreshing= it.mediator?.refresh is LoadState.Loading
            }
        }
        binding.swipe.setOnRefreshListener {
            adapter.refresh()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        binding
    }


    //Option Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_appbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.logout_button -> {
                logout()
                true
            } R.id.language_button -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
                true
            } R.id.map_button -> {
                val intent = Intent(this,MapsActivity::class.java)
                intent.putExtra(EXTRA_USER,userModel)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {

        AlertDialog.Builder(this).apply{
            setMessage(getString(R.string.exit_dialog))
            setPositiveButton(getString(R.string.yes_dialog)){ _, _ ->
                lifecycleScope.launch {
                    pref.logOut()
                }
                val intent = Intent(applicationContext, LoginActivity::class.java)
                Log.e(TAG, "token dihapus")
                startActivity(intent)
                finish()

            }
            setNegativeButton(getString(R.string.no_dialog)){
                    dialog,_ -> dialog.dismiss()
            }
            create()
            show()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object{
        const val USER = "user"
        private const val TAG = "Main Activity Log"
    }


}