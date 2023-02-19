package com.example.final_project_afeka

import android.app.Application
import com.example.final_project_afeka.location.MCT5
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApp: Application(){
    override fun onCreate() {
        super.onCreate()
        MCT5.initHelper()
    }

}