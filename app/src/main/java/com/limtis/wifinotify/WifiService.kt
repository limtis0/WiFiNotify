package com.limtis.wifinotify

import android.app.NotificationManager
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
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi


class WiFiService : Service() {
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var wifiManager: WifiManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var wifiSet: WifiSet

    private val networkCallback = createNetworkCallback()

    override fun onCreate() {
        super.onCreate()

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        wifiManager = getSystemService(WifiManager::class.java)
        notificationManager = getSystemService(NotificationManager::class.java)
        wifiSet = WifiSet(this)
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
        Notifications.createNotificationChannel(notificationManager)
        val foregroundNotification = Notifications.createForegroundNotification(this)
        startForeground(FOREGROUND_SERVICE_ID, foregroundNotification)

        return START_STICKY
    }

    // On service stopped
    override fun onDestroy() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
        super.onDestroy()
    }

    private fun createNetworkCallback(): NetworkCallback {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return createNetworkCallbackAndroid12Plus()
        }
        return createNetworkCallbackBelowAndroid12()
    }

    @Suppress("DEPRECATION")
    private fun createNetworkCallbackBelowAndroid12(): NetworkCallback {
        val context = this

        return object : NetworkCallback() {
            // Called when the framework connects and has declared a new network ready for use
            override fun onAvailable(network: Network) {
                val connectionInfo = wifiManager.connectionInfo
                val ssid = connectionInfo.ssid

                sendNotificationOnNewSSID(context, ssid)
                Toast.makeText(applicationContext, ssid, Toast.LENGTH_SHORT).show()  // TODO: Delete
            }
        }
    }

    @RequiresApi(31)
    private fun createNetworkCallbackAndroid12Plus(): NetworkCallback {
        val context = this

        return object : NetworkCallback(
            FLAG_INCLUDE_LOCATION_INFO
        ) {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)

                val wifiInfo = networkCapabilities.transportInfo as WifiInfo? ?: return
                val ssid = wifiInfo.ssid

                sendNotificationOnNewSSID(context, ssid)
                Toast.makeText(applicationContext, ssid, Toast.LENGTH_SHORT).show()  // TODO: Delete
            }
        }
    }

    private fun sendNotificationOnNewSSID(context: Context, ssid: String) {
        if (wifiSet.contains(ssid)) {
            return
        }

        val notification = Notifications.createWiFiNotification(context)
        notificationManager.notify(WIFI_NOTIFICATION_ID, notification)

        wifiSet.add(ssid)
    }

    companion object {
        const val FOREGROUND_SERVICE_ID = 1
        const val WIFI_NOTIFICATION_ID = 2
    }
}