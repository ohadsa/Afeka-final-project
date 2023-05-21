package com.example.final_project_afeka

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.final_project_afeka.sensors.*
import dagger.hilt.android.AndroidEntryPoint

/*
@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MyReminder.startReminder(this)
        collectAll()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    val viewModel: MainViewModel by viewModels()


    @Inject
    lateinit var auth: FirebaseAuth
    private var shouldHide = false

    private val myRadio: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val json = intent.getStringExtra(LocationService.BROADCAST_NEW_LOCATION_EXTRA_KEY)
            val loc: Loc = Gson().fromJson(json, Loc::class.java)
            viewModel.notifyLocationChanged(loc)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_UP -> {
                if (shouldHide) {
                    currentFocus?.let { focus ->
                        UIUtil.hideKeyboard(this)
                        focus.clearFocus()
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> shouldHide = false
            MotionEvent.ACTION_DOWN -> shouldHide = true
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun startLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null)
            startLoginActivity()
        val intentFilter = IntentFilter(LocationService.BROADCAST_NEW_LOCATION)
        LocalBroadcastManager.getInstance(this).registerReceiver(myRadio, intentFilter)
        registerReceiver(myRadio, intentFilter)
    }

    private fun startService() {
        val intent = Intent(this, LocationService::class.java)
        intent.action = LocationService.START_FOREGROUND_SERVICE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
            val intentFilter = IntentFilter(LocationService.BROADCAST_NEW_LOCATION)
            registerReceiver(myRadio, intentFilter)
        } else {
            startService(intent)
        }
    }
    override fun onDestroy() {
        // Stop the SensorService when the app is closed
        super.onDestroy()
    }

    private fun stopService() {
        val intent = Intent(this, LocationService::class.java)
        intent.action = LocationService.STOP_FOREGROUND_SERVICE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun collectAll() {
        lifecycleScope.launch {
            viewModel.startTime.collect {
                if (it != null)
                    startService()
            }

        }
        lifecycleScope.launch {
            viewModel.finishService.collect {
                if (it) {
                    viewModel.allowService.value = false
                    stopService()
                }
            }

        }
        lifecycleScope.launch {
            viewModel.allowService.collect {
                if (it) {
                    viewModel.finishService.value = false
                    startService()
                }
            }

        }
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myRadio)
        unregisterReceiver(myRadio)
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            if (getIntent().action == LocationService.MAIN_ACTION) {
                // came from notification
            }
        }
    }
}

 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val requestPermissionsCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkPermissions()) {
            startBumpDetectorService()
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        // Check if the necessary permissions are granted
        return ContextCompat.checkSelfPermission(this,ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        // Request necessary permissions
        ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), requestPermissionsCode)
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}


