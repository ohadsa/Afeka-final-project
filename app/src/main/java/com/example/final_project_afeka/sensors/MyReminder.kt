package com.example.final_project_afeka.sensors

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.final_project_afeka.location.ReminderReceiver
import java.util.*

class MyReminder2 {
    companion object {
        @SuppressLint("ServiceCast")
        public fun startReminder(context: Context) {
            var reminderContext = context.getApplicationContext()
            cancelReminder(reminderContext)

            Log.d("pttt", "DeviceBootReceiver startReminder")

            if (true) {
                val manager = reminderContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.add(Calendar.MINUTE, 1)

                manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 5000, 10000, getIntent(reminderContext))
                //AlarmManager.INTERVAL_FIFTEEN_MINUTES
            }
        }

        public fun cancelReminder(context: Context) {
            val reminderContext = context.getApplicationContext()
            val manager = reminderContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.cancel(getIntent(reminderContext))
        }

        private fun getIntent(context: Context): PendingIntent {
            val reminderContext = context.getApplicationContext()
            val alarmIntent = Intent(reminderContext, ReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                    reminderContext,
                    12,
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            return pendingIntent
        }
    }
}