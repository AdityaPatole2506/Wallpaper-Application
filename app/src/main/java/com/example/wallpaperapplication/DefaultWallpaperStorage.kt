package com.example.wallpaperapplication

import android.content.Context
import androidx.core.content.edit

object DefaultWallpaperStorage {

    private const val PREFS_NAME = "default_wallpapers"

    private const val HOME_WALLPAPER =
        "home_wallpaper"

    private const val LOCK_WALLPAPER =
        "lock_wallpaper"

    fun saveHomeWallpaper(
        context: Context,
        uri: String
    ) {
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        ).edit {
            putString(
                HOME_WALLPAPER,
                uri
            )
        }
    }

    fun saveLockWallpaper(
        context: Context,
        uri: String
    ) {
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        ).edit {
            putString(
                LOCK_WALLPAPER,
                uri
            )
        }
    }

    fun getHomeWallpaper(
        context: Context
    ): String? {

        return context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .getString(
                HOME_WALLPAPER,
                null
            )
    }

    fun getLockWallpaper(
        context: Context
    ): String? {

        return context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .getString(
                LOCK_WALLPAPER,
                null
            )
    }
}