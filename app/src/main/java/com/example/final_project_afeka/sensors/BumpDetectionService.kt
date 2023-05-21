package com.example.final_project_afeka.sensors

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.final_project_afeka.MainActivity
import com.example.final_project_afeka.R

class BumpDetectionService : Service(), OnBumpDetect {

    companion object {
        private const val CHANNEL_ID = "BumpNotificationChannel"
        private const val NOTIFICATION_ID = 1
    }

    private lateinit var bumpDetector: BumpDetector

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        bumpDetector = BumpDetector(this, this)
        bumpDetector.startListening()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun invoke() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        bumpDetector.stopListening()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Bump Detected!")
            .setContentText("A bump has been detected by the accelerometer.")
            .setSmallIcon(R.drawable.road_blockade)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Bump Detection Notifications"
            val descriptionText = "Notifications for when a bump is detected by the accelerometer."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}


class BumpDetectorService : Service() {

    private lateinit var bumpDetector: BumpDetector
    private val channelId = "bump_channel_id"

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        bumpDetector = BumpDetector(this, object : OnBumpDetect {
            override fun invoke() {
                showNotification()
            }
        })

        bumpDetector.startListening()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        bumpDetector.stopListening()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Report Hazard"
            val descriptionText = "The location has been saved"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.road_blockade)
            .setContentTitle("Report Hazard")
            .setContentText("The location has been saved")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        startForeground(1, notification)
    }
}
