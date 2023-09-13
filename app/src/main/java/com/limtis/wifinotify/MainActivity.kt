package com.limtis.wifinotify

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.limtis.wifinotify.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var permissionManager: PermissionManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bind default view
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        permissionManager = PermissionManager(this)

        binding.startButton.setOnClickListener {
            val permissionsGranted = permissionManager.checkPermissionsAndRequest()

            val message = if (permissionsGranted) {
                startForegroundService(Intent(this, WiFiService::class.java))
                "Не забудьте отключить оптимизацию батареи для этого приложения"
            } else {
                "Невозможно запустить сервис без разрешений"
            }

            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }
}