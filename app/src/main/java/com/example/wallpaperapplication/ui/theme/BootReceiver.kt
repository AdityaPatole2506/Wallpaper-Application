package com.example.wallpaperapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        if (
            intent.action ==
            Intent.ACTION_BOOT_COMPLETED
        ) {

            if (
                AutoWallpaperPreferences.isEnabled(
                    context
                )
            ) {

                val serviceIntent =
                    Intent(
                        context,
                        WallpaperForegroundService::class.java
                    )
                val intervalMinutes =
                    IntervalStorage
                        .getInterval(
                            context
                        )
                        .toLongOrNull()
                        ?: 15L

                val workRequest =
                    PeriodicWorkRequestBuilder<
                            WallpaperWorker
                            >(
                        intervalMinutes,
                        TimeUnit.MINUTES
                    ).build()

                WorkManager.getInstance(
                    context
                ).enqueueUniquePeriodicWork(
                    "wallpaper_worker",
                    ExistingPeriodicWorkPolicy.UPDATE,
                    workRequest
                )

                androidx.core.content.ContextCompat
                    .startForegroundService(
                        context,
                        serviceIntent
                    )
            }
        }
    }
}