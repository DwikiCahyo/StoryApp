package com.dwiki.storyapp.activity

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.dwiki.storyapp.R
import com.dwiki.storyapp.ResultStory
import com.dwiki.storyapp.ViewModel.MapViewModel


import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.dwiki.storyapp.databinding.ActivityMapsBinding
import com.dwiki.storyapp.model.UserModel
import com.dwiki.storyapp.repository.ViewModelFactoryRepository
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var userModel: UserModel? = null

    private val viewModel: MapViewModel by viewModels{
        ViewModelFactoryRepository.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userModel = intent.getParcelableExtra(EXTRA_USER)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    private fun setStyle() {
        try {
            val styleSuccess = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.map_style))
            Log.e(TAG,"Style implement success")
            if (!styleSuccess){
                Log.e(TAG,"Style parsing failed.")
            }
        } catch (e:Resources.NotFoundException){
                Log.e(TAG,"Can't find style. Error: ", e)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.e("tag","Memaanggil Map")
        Log.e("tag","Memanggil ${userModel?.name}")
        setStyle()



        mMap.uiSettings.isZoomControlsEnabled =true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        showData()
        getNowLocation()


    }

    private fun showData() {
        val boundsBuilder = LatLngBounds.Builder()
        userModel.let { it ->
            if (it != null) {
                viewModel.getMapStories(it.token).observe(this) {
                    if (it != null) {
                        when (it) {
                            is ResultStory.Loading ->{}
                            is ResultStory.Success -> {
                                it.data.forEachIndexed { _, data ->
                                    val LatLng = LatLng(data.lat, data.lon)

                                    mMap.addMarker(
                                        MarkerOptions()
                                            .position(LatLng)
                                            .title(data.name)
                                            .snippet(data.description)
                                            .icon(vectorToBitmap(R.drawable.ic_baseline_home_work_24,Color.RED)))

                                    boundsBuilder.include(LatLng)
                                    val bounds: LatLngBounds = boundsBuilder.build()
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64))
                                }
                            }
                            is ResultStory.Error -> {
                                Log.e("Tag", it.message)
                                Toast.makeText(this,"Ada Error",Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this,"Data Kosong",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getNowLocation()
            }
        }

    private fun getNowLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

   private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object{
        const val EXTRA_USER = "user"
        const val TAG = "Maps Activity"
    }


}