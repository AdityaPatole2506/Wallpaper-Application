package com.example.wallpaperapplication

//IMPORT SECTION
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import android.util.Log
import android.widget.Toast
class ScreenOnReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        if (intent.action == Intent.ACTION_SCREEN_ON) {

            val work =
                OneTimeWorkRequestBuilder<WallpaperWorker>()
                    .build()

            WorkManager.getInstance(context)
                .enqueue(work)
            Toast.makeText(
                context,
                "SCREEN ON RECEIVED",
                Toast.LENGTH_LONG
            ).show()
            Log.d(
                "ScreenOnReceiver",
                "SCREEN ON RECEIVED"
            )
        }
    }
}
