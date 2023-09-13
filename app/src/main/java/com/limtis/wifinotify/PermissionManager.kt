package com.limtis.wifinotify

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest


class PermissionManager(private val activity: Activity) {
    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    fun checkPermissionsAndRequest(): Boolean {
        val locationPermissionGranted = isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)

        if (!locationPermissionGranted) {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQUEST_CODE)
        }

        return isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }
}