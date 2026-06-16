package com.example.wallpaperapplication

import android.content.Context

object AutoWallpaperPreferences {

    private const val PREFS =
        "auto_wallpaper_prefs"

    private const val AUTO_ENABLED =
        "auto_enabled"

    fun setEnabled(
        context: Context,
        enabled: Boolean
    ) {

        context
            .getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
            )
            .edit()
            .putBoolean(
                AUTO_ENABLED,
                enabled
            )
            .apply()
    }

    fun isEnabled(
        context: Context
    ): Boolean {

        return context
            .getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
            )
            .getBoolean(
                AUTO_ENABLED,
                false
            )
    }
}