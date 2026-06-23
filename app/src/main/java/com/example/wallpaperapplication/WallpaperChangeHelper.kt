package com.example.wallpaperapplication

import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import kotlin.random.Random

object WallpaperChangeHelper {

    fun changeRandomWallpaper(
        context: Context
    ) {

        try {

            val photos =
                PhotoStorage
                    .loadPhotos(
                        context
                    )

            if (
                photos.isEmpty()
            ) {
                return
            }

            val randomPhoto =
                photos.random()

            val bitmap =
                BitmapFactory.decodeStream(
                    context.contentResolver
                        .openInputStream(
                            Uri.parse(
                                randomPhoto
                            )
                        )
                )

            if (bitmap != null) {

                WallpaperManager
                    .getInstance(
                        context
                    )
                    .setBitmap(
                        bitmap
                    )
            }

        } catch (
            e: Exception
        ) {

            e.printStackTrace()
        }
    }
}