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
        val GRID_ENABLED = booleanPreferencesKey("grid_enabled")
        val ASPECT_RATIO = intPreferencesKey("aspect_ratio") // 0=4:3, 1=16:9, 2=1:1, 3=Full
        val TIMER_DURATION = intPreferencesKey("timer_duration") // 0=Off, 3, 5, 10 seconds
        val VIDEO_QUALITY = intPreferencesKey("video_quality") // 0=SD, 1=HD, 2=FHD, 3=UHD
        val UI_TRANSPARENCY = intPreferencesKey("ui_transparency") // 0-100%
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
    
    val gridEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.GRID_ENABLED] ?: false
    }
    
    val aspectRatio: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ASPECT_RATIO] ?: 0 // Default: 4:3
    }
    
    val timerDuration: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.TIMER_DURATION] ?: 0 // Default: Off
    }
    
    val videoQuality: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.VIDEO_QUALITY] ?: 1 // Default: HD
    }
    
    val uiTransparency: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.UI_TRANSPARENCY] ?: 70 // Default: 70%
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
    
    suspend fun setGridEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GRID_ENABLED] = enabled
        }
    }
    
    suspend fun setAspectRatio(ratio: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ASPECT_RATIO] = ratio.coerceIn(0, 3)
        }
    }
    
    suspend fun setTimerDuration(duration: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TIMER_DURATION] = duration
        }
    }
    
    suspend fun setVideoQuality(quality: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIDEO_QUALITY] = quality.coerceIn(0, 3)
        }
    }
    
    suspend fun setUITransparency(transparency: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.UI_TRANSPARENCY] = transparency.coerceIn(0, 100)
        }
    }
}
