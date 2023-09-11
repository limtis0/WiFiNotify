package com.limtis.wifinotify

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start the service, if it is not started yet
        val serviceIntent = Intent(this, WiFiService::class.java)
        val isServiceRunning = (PendingIntent.getForegroundService(
            this,
            0,
            serviceIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) != null)

        if (!isServiceRunning) {
            startForegroundService(serviceIntent)  // TODO: Set as foreground
        }

        // Toast message
        val message = if (!isServiceRunning) "Started the service!" else "Service is already running!"

        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, message, duration)
        toast.show()
    }
}