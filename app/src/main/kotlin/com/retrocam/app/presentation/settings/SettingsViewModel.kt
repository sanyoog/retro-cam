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
    
    val gridEnabled: StateFlow<Boolean> = appPreferences.gridEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )
    
    val aspectRatio: StateFlow<Int> = appPreferences.aspectRatio.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
    
    val timerDuration: StateFlow<Int> = appPreferences.timerDuration.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
    
    val videoQuality: StateFlow<Int> = appPreferences.videoQuality.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 1
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
    
    fun toggleGrid(enabled: Boolean) {
        viewModelScope.launch {
            appPreferences.setGridEnabled(enabled)
        }
    }
    
    fun setAspectRatio(ratio: Int) {
        viewModelScope.launch {
            appPreferences.setAspectRatio(ratio)
        }
    }
    
    fun setTimerDuration(duration: Int) {
        viewModelScope.launch {
            appPreferences.setTimerDuration(duration)
        }
    }
    
    fun setVideoQuality(quality: Int) {
        viewModelScope.launch {
            appPreferences.setVideoQuality(quality)
        }
    }
}
