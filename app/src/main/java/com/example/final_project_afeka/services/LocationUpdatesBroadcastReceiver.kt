package com.example.final_project_afeka.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.LocationResult

class LocationUpdatesBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_PROCESS_UPDATES) {
            val result = LocationResult.extractResult(intent)
            if (result != null) {
                val locations = result.locations
                if (locations.isNotEmpty()) {
                    val locationPairs = locations.map { LocationData(it.latitude, it.longitude, it.speed) }
                    val broadcastIntent = Intent(BumpDetectorService.ACTION_LOCATION_UPDATE)
                    broadcastIntent.putParcelableArrayListExtra(BumpDetectorService.EXTRA_LOCATIONS, ArrayList(locationPairs))
                    context.sendBroadcast(broadcastIntent)
                }
            }
        }
    }

    companion object {
        const val ACTION_PROCESS_UPDATES =
            "com.example.project.action.PROCESS_UPDATES"
    }
}
