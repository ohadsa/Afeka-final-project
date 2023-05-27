package com.example.final_project_afeka

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.final_project_afeka.services.BumpDetectorService
import com.example.final_project_afeka.services.BumpDialogActivity
import com.example.final_project_afeka.services.objects.LocationData
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val requestPermissionsCode = 1
    private val REQUEST_LOCATION_PERMISSION_CODE = 1001

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    val viewModel: MainViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getLocationOnce()  {
        if (hasLocationPermissions()) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestLocationPermissions()
            }
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.lastLocation.value =
                        LocationData(location.latitude, location.longitude, location.speed)
                }
            }
        } else {
            requestLocationPermissions()
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handleServiceConfigurations()
        getLocationOnce()
        collectAll()

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun collectAll() {
        lifecycleScope.launch {
            viewModel.startTime.collect {
                if (it != null)
                    startMyService()
            }

        }
        lifecycleScope.launch {
            viewModel.finishService.collect {
                if (it) {
                    viewModel.allowService.value = false
                    viewModel.startService()
                    stopMyService()
                }
            }

        }
        lifecycleScope.launch {
            viewModel.allowService.collect {
                if (it) {
                    viewModel.finishService.value = false
                    startMyService()
                }
            }

        }

        lifecycleScope.launch{
            viewModel.openHazardDialog.collect{
                viewModel.openHazardDialog.value = false
                if(it){
                    val intent = Intent(this@MainActivity, BumpDialogActivity::class.java)
                    intent.putExtra("latitude", viewModel.lastLocation.value?.latitude)
                    intent.putExtra("longitude", viewModel.lastLocation.value?.longitude)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestPermissionsCode -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue the action or workflow in your app.
                    startBumpDetectorService()
                } else {
                    // Explain to the user that the feature is unavailable because the features requires a permission that the user has denied.
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }
    private fun startBumpDetectorService() {
        val serviceIntent = Intent(this, BumpDetectorService::class.java)
        intent.action = BumpDetectorService.START_FOREGROUND_SERVICE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
    private fun stopMyService() {
        val intent = Intent(this, BumpDetectorService::class.java)
        intent.action = BumpDetectorService.STOP_FOREGROUND_SERVICE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startMyService() {
        if (hasLocationPermissions()) {
            startBumpDetectorService()
        } else {
            requestLocationPermissions()
        }
    }
    private fun handleServiceConfigurations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent()
            val packageName = packageName
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }

    }

    private fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                ),
            REQUEST_LOCATION_PERMISSION_CODE
        ).also {
            println("request permission")
        }
    }


}


