package com.example.final_project_afeka.services

import android.Manifest
import android.app.ActivityManager
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
import com.example.final_project_afeka.HAZARDS_TAG_FB
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@AndroidEntryPoint
class BumpDetectorService : Service() {

    private lateinit var bumpDetector: BumpDetector
    private val channelId = "bump_channel_id"
    private var counter = 1
    private var powerManager: PowerManager? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default)

    @Inject
    lateinit var realTimeDB: FirebaseDatabase

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
    var lastNotify: Long = 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent == null) {
            stopForeground(true)
            return START_NOT_STICKY
        }
        lastNotify = Date().time
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

    fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false

        val packageName = context.packageName
        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == packageName) {
                return true
            }
        }

        return false
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
            val text =
                if (latitude == null || longitude == null || latitude == 0.0 || longitude == 0.0) "Remember Turn Lights On!"
                else "Location saved and sent to server!"
            val notification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.road_blockade)
                .setContentTitle(content)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent) // Add the PendingIntent to the notification
                .setAutoCancel(true) // Automatically remove the notification when the user taps it
                .build()
            if (!isAppInForeground(this)) {
                if (Date().time - lastNotify > 5000) {
                    saveHazardInDatabase(latitude, longitude)
                    lastNotify = Date().time
                }
            }
            startForeground(1, notification)
            counter++
        }
    }

    private fun saveHazardInDatabase(latitude: Double?, longitude: Double?) {
        if (latitude == null || longitude == null || latitude == 0.0 || longitude == 0.0) return
        serviceScope.launch {
            saveNewHazard(
                HazardResponse(
                    latitude,
                    longitude,
                )
            )
        }
    }

    // functions to write to database
    private fun generateKey(lon: Double, lat: Double): String {
        val buffer = ByteBuffer.allocate(16).order(ByteOrder.BIG_ENDIAN)
        buffer.putDouble(lon)
        buffer.putDouble(lat)
        val bytes = buffer.array()
        return bytes.joinToString("") { String.format("%02X", it) }
    }

    private fun reverseKey(key: String): Pair<Double, Double> {
        val bytes = key.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        val a = buffer.double
        val b = buffer.double
        return Pair(a, b)
    }

    private fun combinedLocation(
        closeHazards: List<HazardResponse>,
        newHazard: HazardResponse
    ): HazardResponse {
        var totalLat = 0.0
        var totalLon = 0.0
        var totalReports = 0
        var totalLevel = 0
        var verified = false

        for (hazard in closeHazards) {
            totalLat += hazard.lat
            totalLon += hazard.lon
            totalReports += hazard.reports
            totalLevel += hazard.level.toInt().times(hazard.reports)
            verified = verified || hazard.verified
        }

        totalLat += newHazard.lat
        totalLon += newHazard.lon
        totalReports += newHazard.reports
        totalLevel += newHazard.level.toInt()
        verified = verified || newHazard.verified

        val avgLat = totalLat / (closeHazards.size + 1)
        val avgLon = totalLon / (closeHazards.size + 1)
        val avgLevel = (totalLevel.toDouble().div((totalReports + 1).toDouble()))
            .toHazardLevel()

        return HazardResponse(avgLat, avgLon, avgLevel, totalReports, verified)
    }

    private fun calculateDistanceInMeter(loc1: Loc, loc2: Loc): Double {
        val earthRadius = 6371 // radius in kilometers

        val dLat = Math.toRadians(loc2.lat - loc1.lat)
        val dLon = Math.toRadians(loc2.lon - loc1.lon)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(loc1.lat)) * Math.cos(Math.toRadians(loc2.lat)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        val distance = earthRadius * c

        return distance * 1000 // convert to meters
    }

    private suspend fun getAllHazardsIn50MetersRadius(newHazard: HazardResponse): List<HazardResponse> {
        val allHazards: List<HazardResponse> = initHazardsAround()
        val closeHazards: MutableList<HazardResponse> = ArrayList()

        for (hazard in allHazards) {
            val distance = calculateDistanceInMeter(
                Loc(hazard.lat, hazard.lon),
                Loc(newHazard.lat, newHazard.lon)
            )
            if (distance <= 50) {
                closeHazards.add(hazard)
            }
        }

        return closeHazards
    }

    suspend fun saveNewHazard(hazard: HazardResponse) {
        try {
            val closeHazards: List<HazardResponse> = getAllHazardsIn50MetersRadius(hazard)
            val hazardToSave: HazardResponse = combinedLocation(closeHazards, hazard)

            realTimeDB.getReference(HAZARDS_TAG_FB)
                .child(generateKey(hazardToSave.lon, hazardToSave.lat))
                .setValue(hazardToSave)

            // Remove the close hazards.
            for (oldHazard in closeHazards) {
                realTimeDB.getReference(HAZARDS_TAG_FB)
                    .child(generateKey(oldHazard.lon, oldHazard.lat))
                    .removeValue()
            }
        } catch (e: Exception) {
            // Handle the exception, e.g., log the error or show a message to the user.
            println("saveNewHazard Error: ${e.message}")
        }
    }

    private suspend fun initHazardsAround(): List<HazardResponse> =
        suspendCancellableCoroutine { continuation ->
            realTimeDB.getReference(HAZARDS_TAG_FB)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val hazards = mutableListOf<HazardResponse>()
                        for (shot in snapshot.children) {
                            val hazardResponse = shot.getValue(HazardResponse::class.java)
                            hazardResponse?.let {
                                hazards.add(it)
                            }
                        }
                        if (continuation.isActive) {
                            continuation.resume(hazards)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        if (continuation.isActive) {
                            continuation.resumeWithException(Exception(error.toException()))
                        }
                    }
                })
        }


}