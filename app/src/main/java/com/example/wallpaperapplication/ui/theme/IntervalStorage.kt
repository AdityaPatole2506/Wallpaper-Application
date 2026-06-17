package com.example.wallpaperapplication

import android.content.Context

object IntervalStorage {

    private const val PREFS_NAME =
        "interval_prefs"

    private const val INTERVAL_KEY =
        "interval_minutes"

    fun saveInterval(
        context: Context,
        interval: String
    ) {

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .putString(
                INTERVAL_KEY,
                interval
            )
            .apply()
    }

    fun getInterval(
        context: Context
    ): String {

        return context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .getString(
                INTERVAL_KEY,
                "15"
            ) ?: "15"
    }
}