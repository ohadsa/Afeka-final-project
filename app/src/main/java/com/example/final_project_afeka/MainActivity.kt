package com.example.final_project_afeka

import android.content.*
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.final_project_afeka.location.Loc
import com.example.final_project_afeka.location.LocationService
import com.example.final_project_afeka.location.MyReminder
import com.example.final_project_afeka.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : FragmentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MyReminder.startReminder(this)
        collectAll()
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
                if (it){
                    println("collectAll 123")
                    stopService()
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
