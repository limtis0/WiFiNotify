package com.limtis.wifinotify

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED && checkPermissions(context)) {
            val serviceIntent = Intent(context, WiFiService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }

    private fun checkPermissions(context: Context): Boolean {
        val notifications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionManager.isPermissionGrantedWithContext(context, Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true
        }

        val bgLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            PermissionManager.isPermissionGrantedWithContext(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            true
        }

        val location = PermissionManager.isPermissionGrantedWithContext(context, Manifest.permission.ACCESS_FINE_LOCATION)

        return notifications && bgLocation && location
    }
}