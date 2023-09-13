package com.limtis.wifinotify

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.os.Build


class PermissionManager(private val activity: Activity) {
    companion object {
        const val NOTIFICATIONS_PERMISSION_REQUEST_CODE = 1
        const val LOCATION_PERMISSION_REQUEST_CODE = 2
    }

    fun checkPermissionsAndRequest(): Boolean {
        // Android 13+ requires POST_NOTIFICATIONS
        val notificationsPermissionGranted: Boolean

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationsPermissionGranted = isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)

            if (!notificationsPermissionGranted) {
                requestPermission(Manifest.permission.POST_NOTIFICATIONS, NOTIFICATIONS_PERMISSION_REQUEST_CODE)
            }
        } else {
            notificationsPermissionGranted = true
        }

        // Location is needed for getting SSID
        val locationPermissionGranted = isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)

        if (!locationPermissionGranted) {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQUEST_CODE)
        }

        return locationPermissionGranted && notificationsPermissionGranted
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }
}