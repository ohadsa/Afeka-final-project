package com.example.final_project_afeka

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.final_project_afeka.services.objects.MCT5
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class MainApp: Application(){
    override fun onCreate() {
        super.onCreate()
        MCT5.initHelper()
        initializeSharedPreferences()

    }

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private fun initializeSharedPreferences() {
        myPreferences = sharedPreferences
    }

    companion object {
        var myPreferences: SharedPreferences? = null
            private set
    }
}
