package com.example.final_project_afeka.location

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.final_project_afeka.MainActivity
import com.example.final_project_afeka.location.MCT5.CycleTicker
import com.example.final_project_afeka.sensors.BumpDetector
import com.example.final_project_afeka.sensors.OnBumpDetect
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.example.final_project_afeka.R

class LocationService : Service() {

    companion object {

        const val BROADCAST_NEW_LOCATION = "BROADCAST_NEW_LOCATION"
        const val BROADCAST_NEW_LOCATION_EXTRA_KEY = "BROADCAST_NEW_LOCATION_EXTRA_KEY"
        const val START_FOREGROUND_SERVICE = "START_FOREGROUND_SERVICE"
        const val STOP_FOREGROUND_SERVICE = "STOP_FOREGROUND_SERVICE"
        const val NOTIFICATION_ID = 154
        const val CHANNEL_ID = "com.guy.class23a_ands_4.CHANNEL_ID_FOREGROUND"
        const val MAIN_ACTION = "com.guy.class23a_ands_4.locationservice.action.main"

        fun isMyServiceRunning(context: Context): Boolean {
            val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val runs = manager.getRunningServices(Int.MAX_VALUE)
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (LocationService::class.java.name == service.service.className) {
                    return true
                }
            }
            return false
        }

        @TargetApi(26)
        private fun prepareChannel(context: Context, id: String, importance: Int) {
            val appName = context.getString(R.string.app_name)
            val notifications_channel_description = "Cycling map channel"
            val nm = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (nm != null) {
                var nChannel = nm.getNotificationChannel(id)
                if (nChannel == null) {
                    nChannel = NotificationChannel(id, appName, importance)
                    nChannel.description = notifications_channel_description

                    // from another answer
                    nChannel.enableLights(true)
                    nChannel.lightColor = Color.BLUE
                    nm.createNotificationChannel(nChannel)
                }
            }
        }

        fun getNotificationBuilder(
            context: Context, channelId: String, importance: Int,
        ): NotificationCompat.Builder {
            val builder: NotificationCompat.Builder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    prepareChannel(context, channelId, importance)
                    NotificationCompat.Builder(context, channelId)
                } else {
                    NotificationCompat.Builder(context)
                }
            return builder
        }
    }

    private var lastShownNotificationId = -1


    private var notificationBuilder: NotificationCompat.Builder? = null
    private var isServiceRunningRightNow = false

    private var wakeLock: PowerManager.WakeLock? = null
    private var powerManager: PowerManager? = null

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var bumpDetector: BumpDetector? = null
    var loc: Loc? = null
    var lastContent = ""
    private val onBumpDetect: OnBumpDetect = object : OnBumpDetect {
        override fun invoke() {
            val content = "loc = (${
                loc?.lon?.times(1000)?.toInt()?.toDouble()?.div(1000)
            },${loc?.lat?.times(100)?.toInt()?.toDouble()?.div(100)})"
            if (lastContent != content) {
                lastContent = content
                updateNotification(content)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            stopForeground(true)
            return START_NOT_STICKY
        }
        Log.d("pttt", "onStartCommand A")
        if (intent.action == START_FOREGROUND_SERVICE) {
            if (isServiceRunningRightNow) {
                return START_STICKY
            }
            Log.d("pttt", "onStartCommand B")
            if (bumpDetector == null) {
                bumpDetector = BumpDetector(this, onBumpDetect)
                bumpDetector?.startListening()
            }
            isServiceRunningRightNow = true
            //notifyToUserForForegroundService()
            startRecording()
        } else if (intent.action == STOP_FOREGROUND_SERVICE) {
            stopRecording()
            stopForeground(true)
            stopSelf()
            isServiceRunningRightNow = false
            return START_NOT_STICKY
        }
        return START_STICKY
    }


    var counter = 0
    private val cycleTicker: CycleTicker = object : CycleTicker {
        override fun secondly(repeatsRemaining: Int) {
//            counter += 100
//            val content = "Drive safe ! \ntap to back to app"
//            updateNotification(content)
        }

        override fun done() {}
    }

    @SuppressLint("MissingPermission")
    private fun startRecording() {
        // Keep CPU working
        bumpDetector?.startListening()

        powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PassiveApp:tag")
        wakeLock?.acquire()
        MCT5.get().cycle(cycleTicker, MCT5.CONTINUOUSLY_REPEATS, 500)

        // Run GPS
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest = LocationRequest.create()
            locationRequest.smallestDisplacement = 0.5f
            locationRequest.interval = 1000
            locationRequest.fastestInterval = 500
            //locationRequest.setMaxWaitTime(TimeUnit.MINUTES.toMillis(1));
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            // new Google API SDK v11 uses getFusedLocationProviderClient(this)
            fusedLocationProviderClient?.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.myLooper())
        }
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
                val json = Gson().toJson(loc)
                intent.putExtra(BROADCAST_NEW_LOCATION_EXTRA_KEY, json)
                LocalBroadcastManager.getInstance(this@LocationService).sendBroadcast(intent)
                println("loaction = " + loc2.lon + " , " + loc2.lat)
            } else {
                Log.d("pttt", "Location information isn't available.")
            }
        }

        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            super.onLocationAvailability(locationAvailability)
        }
    }

    private fun stopRecording() {
        // Release CPU Holding
        if (wakeLock != null) {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        }
        MCT5.get().remove(cycleTicker)
        bumpDetector?.stopListening()
        // Stop GPS
        if (fusedLocationProviderClient != null) {
            val task = fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
            task?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("pttt", "stop Location Callback removed.")
                    stopSelf()
                } else {
                    Log.d("pttt", "stop Failed to remove Location Callback.")
                }
            }
        }
    }

    private fun updateNotification(content: String) {
        notificationBuilder?.setContentText(content)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID,
            notificationBuilder?.build())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    // // // // // // // // // // // // // // // // Notification  // // // // // // // // // // // // // // //

    // // // // // // // // // // // // // // // // Notification  // // // // // // // // // // // // // // //
    private fun notifyToUserForForegroundService() {
        // On notification click
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.action = MAIN_ACTION
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP


        val pendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        notificationBuilder = getNotificationBuilder(this,
            CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_LOW) //Low importance prevent visual appearance for this notification channel on top
        notificationBuilder?.setContentIntent(pendingIntent) // Open activity
            ?.setOngoing(true)
            ?.setSmallIcon(R.drawable.road_blockade)
            ?.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.road_blockade))
            ?.setContentTitle("Report New Hazard")
            ?.setContentText("The location has been saved, tap to send the report")

        val notification = notificationBuilder?.build()
        startForeground(NOTIFICATION_ID, notification)
        if (NOTIFICATION_ID != lastShownNotificationId) {
            // Cancel previous notification
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(lastShownNotificationId)
        }
        lastShownNotificationId = NOTIFICATION_ID
    }


}