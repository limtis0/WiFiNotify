package com.limtis.wifinotify

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.os.IBinder
import android.util.Log
import android.widget.Toast

class WiFiService : Service() {
    private lateinit var connectivityManager: ConnectivityManager

    override fun onCreate() {
        super.onCreate()
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    // Required override for Service() implementation
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    // On service started
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        return START_STICKY
    }

    // On service stopped
    override fun onDestroy() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
        super.onDestroy()
    }

    // Callback on network updates
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // Called when the framework connects and has declared a new network ready for use
        override fun onAvailable(network: Network) {
            val message = "Wi-Fi triggered"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(applicationContext, message, duration)
            toast.show()

            // Log.d("WiFiService", "SSID: ${getSSID(network)}")
        }
    }

    private fun getSSID(network: Network): String {
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        // If connected to Wi-Fi
        if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
            val wifiInfo = networkCapabilities.transportInfo as WifiInfo
            return wifiInfo.ssid ?: ""
        }

        return ""
    }
}