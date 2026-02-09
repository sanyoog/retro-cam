package com.retrocam.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.retrocam.app.data.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences
) : ViewModel() {
    
    val soundEnabled: StateFlow<Boolean> = appPreferences.soundEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )
    
    val hapticsEnabled: StateFlow<Boolean> = appPreferences.hapticsEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )
    
    val photoQuality: StateFlow<Int> = appPreferences.photoQuality.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 2
    )
    
    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            appPreferences.setSoundEnabled(enabled)
        }
    }
    
    fun toggleHaptics(enabled: Boolean) {
        viewModelScope.launch {
            appPreferences.setHapticsEnabled(enabled)
        }
    }
    
    fun setPhotoQuality(quality: Int) {
        viewModelScope.launch {
            appPreferences.setPhotoQuality(quality)
        }
    }
}
