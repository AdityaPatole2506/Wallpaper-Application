package com.example.wallpaperapplication

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore("photo_storage")

object PhotoStorage {

    private val PHOTO_URIS =
        stringSetPreferencesKey("photo_uris")

    suspend fun savePhotos(
        context: Context,
        uris: List<String>
    ) {
        context.dataStore.edit { prefs ->
            prefs[PHOTO_URIS] = uris.toSet()
        }
    }

    suspend fun loadPhotos(
        context: Context
    ): List<String> {

        val prefs =
            context.dataStore.data.first()

        return prefs[PHOTO_URIS]
            ?.toList()
            ?: emptyList()
    }
}