package com.example.final_project_afeka.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.final_project_afeka.MainActivity
import com.example.final_project_afeka.R
import com.example.final_project_afeka.services.LocationUpdatesBroadcastReceiver.Companion.ACTION_PROCESS_UPDATES
import com.example.final_project_afeka.services.objects.BumpDetector
import com.example.final_project_afeka.services.objects.Loc
import com.example.final_project_afeka.services.objects.LocationData
import com.example.final_project_afeka.services.objects.MCT5
import com.example.final_project_afeka.services.objects.OnBumpDetect
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class BumpDetectorService : Service() {

    private lateinit var bumpDetector: BumpDetector
    private val channelId = "bump_channel_id"
    private var counter = 1
    private var powerManager: PowerManager? = null
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        const val ACTION_LOCATION_UPDATE = "com.example.project.action.LOCATION_UPDATE"
        const val EXTRA_LOCATIONS = "com.example.project.extra.LOCATIONS"
        const val BROADCAST_NEW_LOCATION = "BROADCAST_NEW_LOCATION"
        const val START_FOREGROUND_SERVICE = "START_FOREGROUND_SERVICE"
        const val STOP_FOREGROUND_SERVICE = "STOP_FOREGROUND_SERVICE"

    }

    private val cycleTicker: MCT5.CycleTicker = object : MCT5.CycleTicker {
        override fun secondly(repeatsRemaining: Int) {
            Log.d(TAG, "secondly: repeatsRemaining: $repeatsRemaining")
        }

        override fun done() {}
    }

    private fun startRecording() {
        bumpDetector.startListening()

        powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PassiveApp:tag")
        wakeLock?.acquire()
        MCT5.get().cycle(cycleTicker, MCT5.CONTINUOUSLY_REPEATS, 500)

        // Run GPS
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest = LocationRequest.create()
            locationRequest.smallestDisplacement = 0.5f
            locationRequest.interval = 100
            locationRequest.fastestInterval = 500
            //locationRequest.setMaxWaitTime(TimeUnit.MINUTES.toMillis(1));
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var lastLocation: LocationData? = null
    private val locationRequest: LocationRequest by lazy {
        LocationRequest.create().apply {
            interval = 500
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
    val receiver = LocationUpdatesBroadcastReceiver()
    val intentFilter = IntentFilter(ACTION_PROCESS_UPDATES)

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_LOCATION_UPDATE) {
                lastLocation = intent.getParcelableArrayListExtra<LocationData>(EXTRA_LOCATIONS)
                    ?.firstNotNullOf { it }
                println("onReceive lastLocation = $lastLocation")

            }
        }
    }
    var loc: Loc? = null

    private fun wakeUpApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if (locationResult.lastLocation != null) {
                Log.d("pttt", ":getLastLocation")
                val intent = Intent(BROADCAST_NEW_LOCATION)
                val loc2 = Loc()
                    .setLat(locationResult.lastLocation!!.latitude)
                    .setLon(locationResult.lastLocation!!.longitude)
                    .setSpeed(locationResult.lastLocation!!.speed / 3.6)
                loc = loc2
                lastLocation = LocationData(loc2.lat, loc2.lon, loc2.speed.toFloat())
                intent.putExtra(EXTRA_LOCATIONS, lastLocation)
                sendBroadcast(intent)
                println("164 location= (" + loc2.lon + "," + loc2.lat + ") , s=" + loc2.speed)
            } else {
                println("Location information isn't available.")
            }
        }
    }

    private fun stopRecording() {
        if (wakeLock != null) {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        }
        MCT5.get().remove(cycleTicker)
        bumpDetector.stopListening()
        // Stop GPS
        val task = fusedLocationClient.removeLocationUpdates(locationCallback)
        task.addOnCompleteListener { t ->
            if (t.isSuccessful) {
                Log.d("pttt", "stop Location Callback removed.")
                stopSelf()
            } else {
                Log.d("pttt", "stop Failed to remove Location Callback.")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            println("permission failure")
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver, intentFilter)
        registerReceiver(receiver, intentFilter)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        bumpDetector = BumpDetector(this, object : OnBumpDetect {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun invoke(latitude: Double, longitude: Double) {
                showNotification(
                    latitude,
                    longitude
                ) // Update your showNotification method to accept latitude and longitude parameters
                wakeUpApp()
                val dialogIntent = Intent(this@BumpDetectorService, BumpDialogActivity::class.java)
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                // Put latitude and longitude in the intent extras
                dialogIntent.putExtra("latitude", latitude)
                dialogIntent.putExtra("longitude", longitude)
                startActivity(dialogIntent)
            }

            override fun getLocation(): Pair<Double, Double> {
                return lastLocation?.let { Pair(it.latitude, it.longitude) } ?: Pair(0.0, 0.0)
            }
        })

        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock =
            powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BumpDetectorService::tag")
        wakeLock?.acquire()
    }

    var isServiceRunningRightNow = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent == null) {
            stopForeground(true)
            return START_NOT_STICKY
        }
        showNotification(lastLocation?.latitude, lastLocation?.longitude)
        bumpDetector.startListening()
        requestLocationUpdates()
        if (intent.action == START_FOREGROUND_SERVICE) {
            if (isServiceRunningRightNow) {
                return START_STICKY
            }
            isServiceRunningRightNow = true
            startRecording()
            intent
        } else if (intent.action == STOP_FOREGROUND_SERVICE) {
            stopRecording()
            stopForeground(true)
            stopSelf()
            isServiceRunningRightNow = false
            return START_NOT_STICKY
        }

        return START_STICKY
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(getPendingIntent())
    }

    override fun onDestroy() {
        super.onDestroy()
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
        bumpDetector.stopListening()
        stopLocationUpdates() // Stop location updates
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Bump Notification Channel"
            val descriptionText = "Channel for bump notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, LocationUpdatesBroadcastReceiver::class.java)
        intent.action = ACTION_PROCESS_UPDATES
        // Make the PendingIntent immutable.
        return PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, you may want to ask for it
            return
        }

        val task = fusedLocationClient.requestLocationUpdates(
            locationRequest,
            getPendingIntent()
        )

        try {
            task.addOnSuccessListener {
                Log.d(TAG, "Successfully requested location updates")
            }
            task.addOnFailureListener { e ->
                Log.e(TAG, "Requesting location updates failed", e)
            }
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun showNotification(latitude: Double?, longitude: Double?) {
        // Check if we have the necessary permissions
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            // Get the last known location

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            val content =
                if (latitude == null || longitude == null || latitude == 0.0 || longitude == 0.0) "Drive Safe!"
                else "Hazards ahead!"
            val text =  if (latitude == null || longitude == null || latitude == 0.0 || longitude == 0.0) "Remember Turn Lights On!"
            else "Location saved and sent to server!"
            val notification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.road_blockade)
                .setContentTitle(content)
                .setContentText( text )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent) // Add the PendingIntent to the notification
                .setAutoCancel(true) // Automatically remove the notification when the user taps it
                .build()

            startForeground(1, notification)
            counter++
        }
    }


}