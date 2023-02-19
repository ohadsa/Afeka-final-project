package com.example.final_project_afeka.utils.permissions

import android.Manifest.permission.*

sealed class Permission(vararg val permissions: String) {
    object Camera : Permission(CAMERA)
    object MandatoryForFeatureOne : Permission(WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION)
    object LocationAndNotifications :
        Permission(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, POST_NOTIFICATIONS)

    object Storage : Permission(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)

    companion object {
        fun from(permission: String) = when (permission) {
            ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, POST_NOTIFICATIONS -> LocationAndNotifications
            WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE -> Storage
            CAMERA -> Camera
            else -> throw IllegalArgumentException("Unknown permission: $permission")
        }
    }
}