package com.limtis.wifinotify

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat

class Notifications {
    companion object {
        private const val CHANNEL_ID = "WiFiNotify"
        private const val CHANNEL_NAME = "Wi-Fi Notify"
        private const val CHANNEL_DEFAULT_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH

        fun createNotificationChannel(notificationManager: NotificationManager) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME,
                CHANNEL_DEFAULT_IMPORTANCE
            )
            notificationManager.createNotificationChannel(serviceChannel)
        }

        fun createForegroundNotification(context: Context): Notification {
            val notificationTitle = "Wi-Fi Notify - Service is running"

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(),
                PendingIntent.FLAG_IMMUTABLE
            )

            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(notificationTitle)
                // .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setOngoing(false)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            return notificationBuilder.build()
        }

        fun createWiFiNotification(context: Context): Notification {
            val intent: Intent
            val clickAction: PendingIntent
            val notificationText: String

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                clickAction = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                notificationText = "Нажмите, чтобы открыть настройки"
            } else {
                // If Android version is lower than Q, turn off Wi-Fi
                clickAction = createWiFiOffPendingIntent(context)
                notificationText = "Нажмите, чтобы выключить Wi-Fi"
            }

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(androidx.core.R.drawable.notification_icon_background)  // TODO: Add Icons
                .setContentTitle("Если не выбрал с тарификацией трафика - срочно выключи Wi-Fi")
                .setContentText(notificationText)
                .setContentIntent(clickAction)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            return builder.build()
        }

        private fun createWiFiOffPendingIntent(context: Context): PendingIntent {
            // Create an intent that will be triggered when the notification is clicked
            val intent = Intent(context, WiFiOffReceiver::class.java).apply {
                action = WiFiOffReceiver.TURN_OFF_WIFI_ACTION
            }

            return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }
}

@Suppress("DEPRECATION")
class WiFiOffReceiver : BroadcastReceiver() {
    companion object {
        const val TURN_OFF_WIFI_ACTION = "TURN_OFF_WIFI"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == TURN_OFF_WIFI_ACTION) {
            val wifiManager =
                context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.isWifiEnabled = false
        }
    }
}