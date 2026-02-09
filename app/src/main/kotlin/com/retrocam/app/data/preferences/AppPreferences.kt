package com.retrocam.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class AppPreferences @Inject constructor(
    private val context: Context
) {
    private object PreferencesKeys {
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val PHOTO_QUALITY = intPreferencesKey("photo_quality") // 0=Low, 1=Medium, 2=High
        val SAVE_TO_DEVICE = booleanPreferencesKey("save_to_device")
    }
    
    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SOUND_ENABLED] ?: true
    }
    
    val hapticsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.HAPTICS_ENABLED] ?: true
    }
    
    val photoQuality: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PHOTO_QUALITY] ?: 2 // Default: High
    }
    
    val saveToDevice: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SAVE_TO_DEVICE] ?: true
    }
    
    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }
    
    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTICS_ENABLED] = enabled
        }
    }
    
    suspend fun setPhotoQuality(quality: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PHOTO_QUALITY] = quality.coerceIn(0, 2)
        }
    }
    
    suspend fun setSaveToDevice(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SAVE_TO_DEVICE] = enabled
        }
    }
}
