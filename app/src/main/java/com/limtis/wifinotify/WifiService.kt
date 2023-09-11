package com.limtis.wifinotify

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


class WiFiService : Service() {
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var wifiManager: WifiManager

    private val networkCallback = createNetworkCallback()

    override fun onCreate() {
        super.onCreate()

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        wifiManager = getSystemService(WifiManager::class.java)
    }

    // Required override for Service() implementation
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    // On service started
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Register a callback
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        // Start as Foreground Service
        createNotificationChannel()
        val foregroundNotification = createForegroundNotification()
        startForeground(FOREGROUND_SERVICE_ID, foregroundNotification)

        return START_STICKY
    }

    // On service stopped
    override fun onDestroy() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
        super.onDestroy()
    }

    private fun createNetworkCallback(): NetworkCallback {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            return createNetworkCallbackAndroid12Plus()
        }
        return createNetworkCallbackBelowAndroid12()
    }

    @Suppress("DEPRECATION")
    private fun createNetworkCallbackBelowAndroid12(): NetworkCallback {
        return object : NetworkCallback() {
            // Called when the framework connects and has declared a new network ready for use
            override fun onAvailable(network: Network) {
                val connectionInfo = wifiManager.connectionInfo
                val ssid = connectionInfo.ssid

                Toast.makeText(applicationContext, ssid, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(31)
    private fun createNetworkCallbackAndroid12Plus(): NetworkCallback {
        return object : NetworkCallback(
            FLAG_INCLUDE_LOCATION_INFO
        ) {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)

                val wifiInfo = networkCapabilities.transportInfo as WifiInfo?
                val ssid = wifiInfo?.ssid ?: "0"

                Toast.makeText(applicationContext, ssid, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
            CHANNEL_DEFAULT_IMPORTANCE)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    private fun createForegroundNotification(): Notification {
        val notificationTitle = "Wi-Fi Notify Service is running"

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(notificationTitle)
            // .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(false)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        return notificationBuilder.build()
    }

    companion object {
        const val CHANNEL_ID = "WiFiNotify"
        const val CHANNEL_NAME = "Wi-Fi Notify"
        const val CHANNEL_DEFAULT_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH

        const val FOREGROUND_SERVICE_ID = 1
    }
}