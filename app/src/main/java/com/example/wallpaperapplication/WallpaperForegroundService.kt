package com.example.wallpaperapplication

//IMPORT SECTION
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class WallpaperForegroundService : Service() {

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        val notification: Notification =
            NotificationCompat.Builder(
                this,
                "wallpaper_channel"
            )
                .setContentTitle("Wallpaper Application")
                .setContentText("Auto Wallpaper Running")
                .setSmallIcon(android.R.drawable.ic_menu_gallery)
                .setOngoing(true)
                .build()

        startForeground(
            1,
            notification
        )
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        return START_STICKY
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = null

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel =
                NotificationChannel(
                    "wallpaper_channel",
                    "Wallpaper Service",
                    NotificationManager.IMPORTANCE_LOW
                )

            val manager =
                getSystemService(
                    NotificationManager::class.java
                )

            manager.createNotificationChannel(channel)
        }
    }
}