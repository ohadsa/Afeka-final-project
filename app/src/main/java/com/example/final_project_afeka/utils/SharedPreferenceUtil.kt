package com.example.final_project_afeka.utils

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import androidx.core.content.edit
import com.example.final_project_afeka.MainApp
import com.example.final_project_afeka.R
import com.example.final_project_afeka.services.objects.Sensitivity

internal object SharedPreferenceUtil {

    const val KEY_FOREGROUND_ENABLED = "tracking_foreground_location"
    const val START_TIME_TAG = "startTimeTag"

    fun getLocationTrackingPref(context: Context): Boolean =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
            .getBoolean(KEY_FOREGROUND_ENABLED, false)

    fun saveLocationTrackingPref(context: Context, requestingLocationUpdates: Boolean) =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        ).edit {
            putBoolean(KEY_FOREGROUND_ENABLED, requestingLocationUpdates)
        }


    const val KEY_SHAKE_THRESHOLD = "shake_threshold"

    fun readSensitivity(): Sensitivity {
        val sharedPreferences = MainApp.myPreferences ?:return Sensitivity.Normal
        val sensitivityOrdinal = sharedPreferences.getInt(KEY_SHAKE_THRESHOLD, Sensitivity.Normal.ordinal)
        return Sensitivity.values().getOrElse(sensitivityOrdinal) { Sensitivity.Normal }
    }

    // Write Sensitivity value to SharedPreferences
    fun writeSensitivity(sensitivity: Sensitivity) {
        val sharedPreferences = MainApp.myPreferences ?: return
        val editor = sharedPreferences.edit()
        editor.putInt(KEY_SHAKE_THRESHOLD, sensitivity.ordinal)
        editor.apply()
    }

}

fun Location?.toText(): String {
    return if (this != null) {
        "($latitude, $longitude)"
    } else {
        "Unknown location"
    }
}