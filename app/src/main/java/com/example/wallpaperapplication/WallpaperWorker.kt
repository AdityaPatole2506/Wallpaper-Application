package com.example.wallpaperapplication

import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlin.random.Random

class WallpaperWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    // =========================
    // APPLY DEFAULT WALLPAPERS
    // =========================

    private fun applyDefaultWallpapers() {

        try {

            val wm =
                WallpaperManager.getInstance(
                    applicationContext
                )

            DefaultWallpaperStorage
                .getHomeWallpaper(
                    applicationContext
                )
                ?.let { uri ->

                    val bitmap =
                        BitmapFactory.decodeStream(
                            applicationContext
                                .contentResolver
                                .openInputStream(
                                    Uri.parse(uri)
                                )
                        )

                    wm.setBitmap(
                        bitmap,
                        null,
                        true,
                        WallpaperManager.FLAG_SYSTEM
                    )
                }

            DefaultWallpaperStorage
                .getLockWallpaper(
                    applicationContext
                )
                ?.let { uri ->

                    val bitmap =
                        BitmapFactory.decodeStream(
                            applicationContext
                                .contentResolver
                                .openInputStream(
                                    Uri.parse(uri)
                                )
                        )

                    wm.setBitmap(
                        bitmap,
                        null,
                        true,
                        WallpaperManager.FLAG_LOCK
                    )
                }

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    // =========================
    // WORKER
    // =========================

    override suspend fun doWork(): Result {

        return try {

            val savedUris =
                PhotoStorage.loadPhotos(
                    applicationContext
                )

            // No wallpapers selected
            if (savedUris.isEmpty()) {

                applyDefaultWallpapers()

                return Result.success()
            }

            val randomUri =
                Uri.parse(
                    savedUris[
                        Random.nextInt(
                            savedUris.size
                        )
                    ]
                )

            val inputStream =
                applicationContext
                    .contentResolver
                    .openInputStream(
                        randomUri
                    )

            val bitmap =
                BitmapFactory.decodeStream(
                    inputStream
                )

            WallpaperManager
                .getInstance(
                    applicationContext
                )
                .setBitmap(
                    bitmap,
                    null,
                    true,
                    WallpaperManager.FLAG_SYSTEM
                )

            Result.success()

        } catch (e: Exception) {

            e.printStackTrace()

            // Worker failed
            applyDefaultWallpapers()

            Result.failure()
        }
    }
}