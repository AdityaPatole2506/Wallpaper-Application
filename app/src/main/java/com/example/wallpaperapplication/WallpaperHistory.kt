package com.example.wallpaperapplication

import android.content.Context
import androidx.core.content.edit

object WallpaperHistory {

    private const val PREFS_NAME = "wallpaper_history"
    private const val KEY_HISTORY = "history"

    fun saveWallpaper(
        context: Context,
        uri: String
    ) {
        val prefs =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        val current =
            prefs.getStringSet(
                KEY_HISTORY,
                emptySet()
            )?.toMutableSet()
                ?: mutableSetOf()

        current.add(uri)

        prefs.edit {
            putStringSet(
                KEY_HISTORY,
                current
            )
        }
    }

    fun loadHistory(
        context: Context
    ): List<String> {

        val prefs =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        return prefs.getStringSet(
            KEY_HISTORY,
            emptySet()
        )?.toList()
            ?: emptyList()
    }
}