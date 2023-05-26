package com.dwiki.storyapp.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dwiki.storyapp.MainActivity
import com.dwiki.storyapp.R
import com.dwiki.storyapp.ResultStory
import com.dwiki.storyapp.Utils
import com.dwiki.storyapp.ViewModel.NewStoryViewModel
import com.dwiki.storyapp.api.response.ResponseNewStory
import com.dwiki.storyapp.databinding.ActivityAddNewStoryBinding
import com.dwiki.storyapp.model.UserModel
import com.dwiki.storyapp.repository.ViewModelFactoryRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddNewStory : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewStoryBinding
    private lateinit var userModel: UserModel
    private lateinit var photoPath: String
    private var getFile: File? = null
    private lateinit var result:Bitmap
    private var location:Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: NewStoryViewModel by viewModels {
        ViewModelFactoryRepository.getInstance(this)
    }

        override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.no_permission), Toast.LENGTH_SHORT).show()
                finish()
            }
         }
        }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext,it) == PackageManager.PERMISSION_GRANTED
}

    private fun getPermission() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewStoryBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)

        userModel = intent.getParcelableExtra(USER)!!
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getPermission()

        binding.apply {
            galleryImage.setOnClickListener { getGallery() }
            buttonSubmit.setOnClickListener { addStory() }
            openCamera.setOnClickListener {
                takePhoto()
            }
        }
        getMyLocation()

    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        Utils.createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddNewStory,
                "com.dwiki.storyapp",
                it
            )
            photoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherCamera.launch(intent)
        }
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(photoPath)
            getFile = myFile
            showLocation(false)
            result = BitmapFactory.decodeFile(getFile?.path)
            binding.imageAddStory.setImageBitmap(result)
        }
    }

    private fun addStory() {
        if (getFile != null) {
            val file = Utils.reduceFileImage(getFile as File)
            val setDescription = binding.intEditText.text.toString()
            val description = setDescription.toRequestBody("text/plain".toMediaType())
            val reqImage = file.asRequestBody("image/jpg".toMediaTypeOrNull())
            val imageMultipart = MultipartBody.Part.createFormData("photo", file.name, reqImage)
            var lat: RequestBody? = null
            var lon: RequestBody? = null

            if (location != null) {
                lat = location?.latitude.toString().toRequestBody("text/plain".toMediaType())
                lon = location?.longitude.toString().toRequestBody("text/plain".toMediaType())
            }

                if (setDescription.isNotEmpty()) {
                viewModel.postNewStory(userModel.token,description,imageMultipart,lat,lon).observe(this){
                    when (it){
                        is ResultStory.Loading -> {
                            showLoading(true)
                        } is ResultStory.Success -> {
                            showLoading(false)
                            Log.e("Tag", it.data.message)
                            dialogSuccess(it)
                        } is ResultStory.Error -> {
                             showLoading(false)
                             Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }


            } else {
                Toast.makeText(applicationContext, getString(R.string.add_desc), Toast.LENGTH_SHORT).show()
            }
        } else  {
            Toast.makeText(applicationContext, getString(R.string.no_file), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun dialogSuccess(it: ResultStory.Success<ResponseNewStory>) {
        if (it.data.message == "Story created successfully") {
            AlertDialog.Builder(this).apply {
                setMessage(getString(R.string.success_add))
                setPositiveButton(getString(R.string.next_add)) { _, _ ->
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(
                        intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this@AddNewStory as Activity)
                            .toBundle()
                    )
                    Toast.makeText(this@AddNewStory,getString(R.string.scroll),Toast.LENGTH_SHORT).show()
                    finish()
                }
                create()
                show()
            }
        }
    }

    private fun getGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Chosee a picture")
        intentGallery.launch(chooser)
    }

    private fun showLoading(isLoading:Boolean) {
            binding.apply {
                if (isLoading) progressBar.visibility = View.VISIBLE
                else progressBar.visibility = View.GONE
            }
    }

    private fun showLocation(isLocation:Boolean){
        binding.apply {
            if(isLocation) tvLocation.visibility = View.VISIBLE
            else tvLocation.visibility = View.GONE
        }
    }

    private val intentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectImage: Uri = it.data?.data as Uri
            val file = Utils.uriToFile(selectImage, this)
            getFile = file
            showLocation(false)
            binding.imageAddStory.setImageURI(selectImage)

        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    location = it
                    showLocation(true)
                } else {
                    showLocation(false)
                    Toast.makeText(this,"Error to detect location",Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        Log.d("tag", "$it")
        if (it[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            getMyLocation()
        } else  Toast.makeText(this,"Eror",Toast.LENGTH_SHORT).show()
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 100
        const val USER = "user"
    }
}