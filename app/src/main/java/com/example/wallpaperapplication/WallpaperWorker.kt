package com.example.wallpaperapplication

//IMPORT SECTION
import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.net.Uri
import kotlin.random.Random

class WallpaperWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        android.os.Handler(
            android.os.Looper.getMainLooper()
        ).post {
            android.widget.Toast.makeText(
                applicationContext,
                "Worker Running",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
        android.util.Log.d(
            "WallpaperWorker",
            "Worker Started"
        )
        return try {

            val savedUris =
                PhotoStorage.loadPhotos(applicationContext)

            if (savedUris.isEmpty()) {
                return Result.success()
            }

            val randomUri =
                Uri.parse(
                    savedUris[
                        Random.nextInt(savedUris.size)
                    ]
                )

            val inputStream =
                applicationContext.contentResolver
                    .openInputStream(randomUri)

            val bitmap =
                BitmapFactory.decodeStream(inputStream)

            WallpaperManager
                .getInstance(applicationContext)
                .setBitmap(
                    bitmap,
                    null,
                    true,
                    WallpaperManager.FLAG_SYSTEM
                )

            android.os.Handler(
                android.os.Looper.getMainLooper()
            ).post {
                android.widget.Toast.makeText(
                    applicationContext,
                    "Wallpaper Changed",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }

            Result.success()
        } catch (e: Exception) {

            android.os.Handler(
                android.os.Looper.getMainLooper()
            ).post {
                android.widget.Toast.makeText(
                    applicationContext,
                    e.message ?: "Unknown Error",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }

            e.printStackTrace()

            Result.failure()
        }}}